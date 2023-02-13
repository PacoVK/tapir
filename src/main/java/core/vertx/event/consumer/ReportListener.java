package core.vertx.event.consumer;

import core.backend.SearchService;
import core.terraform.Module;
import core.upload.FormData;
import extensions.core.Report;
import extensions.docs.report.TerraformDocumentation;
import extensions.security.report.TfSecReport;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import io.vertx.mutiny.core.eventbus.EventBus;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;

@ApplicationScoped
public class ReportListener {

  static final Logger LOGGER = Logger.getLogger(ReportListener.class.getName());

  SearchService searchService;
  EventBus eventBus;

  public ReportListener(Instance<SearchService> searchServiceInstance, EventBus eventBus) {
    this.searchService = searchServiceInstance.get();
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
    Map<String, List<TfSecReport.TfSecResult>> securityReport = eventBus
            .<Map<String, List<TfSecReport.TfSecResult>>>requestAndAwait(
                    "module.security.report",
                    archive
            )
            .body();
    TerraformDocumentation documentation = eventBus
            .<TerraformDocumentation>requestAndAwait("module.documentation.generate", archive)
            .body();
    report.setSecurityReport(securityReport);
    report.setDocumentation(documentation);
    eventBus.requestAndForget("module.report.finished", report);
    searchService.ingestSecurityScanResult(report);
    return "ok";
  }

  @ConsumeEvent("module.report.finished")
  public String handleReportGenerationFinished(Report report) throws Exception {
    LOGGER.info(String.format("Publishing scan result for module %s, version %s",
            report.getModuleName(),
            report.getModuleVersion())
    );
    searchService.ingestSecurityScanResult(report);
    return "ok";
  }
}
