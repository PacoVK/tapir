package core.vertx.event.consumer;

import core.backend.TapirRepository;
import core.terraform.Module;
import core.upload.FormData;
import extensions.core.Report;
import extensions.docs.report.TerraformDocumentation;
import extensions.security.report.SecurityFinding;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@ApplicationScoped
public class ReportListener {

  static final Logger LOGGER = Logger.getLogger(ReportListener.class.getName());

  TapirRepository tapirRepository;
  EventBus eventBus;

  public ReportListener(Instance<TapirRepository> searchServiceInstance, EventBus eventBus) {
    this.tapirRepository = searchServiceInstance.get();
    this.eventBus = eventBus;
  }

  @Blocking
  @ConsumeEvent("module.extract.finished")
  public String handleArchiveExtractionFinished(FormData archive) throws Exception {
    Module module = archive.getEntity();
    Report report = new Report(
            module.getNamespace(),
            module.getName(),
            module.getProvider(),
            module.getCurrentVersion()
    );
    Map<String, List<SecurityFinding>> securityReport = eventBus
            .<Map<String, List<SecurityFinding>>>requestAndAwait(
                    "module.security.report",
                    archive,
                    new DeliveryOptions().setSendTimeout(150000L)
            )
            .body();
    TerraformDocumentation documentation = eventBus
            .<TerraformDocumentation>requestAndAwait("module.documentation.generate", archive)
            .body();
    report.setSecurityReport(securityReport);
    report.setDocumentation(documentation);
    eventBus.requestAndForget("module.report.finished", report);
    return "ok";
  }

  @ConsumeEvent("module.report.finished")
  public String handleReportGenerationFinished(Report report) throws Exception {
    LOGGER.info(String.format("Publishing scan result for module %s, version %s",
            report.getModuleName(),
            report.getModuleVersion())
    );
    tapirRepository.ingestSecurityScanResult(report);
    return "ok";
  }
}
