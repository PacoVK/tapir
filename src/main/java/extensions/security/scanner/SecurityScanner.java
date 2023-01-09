package extensions.security.scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.upload.FormData;
import extensions.cli.CliCommandProcessor;
import extensions.core.Report;
import extensions.security.report.TfSecReport;
import extensions.security.util.TfSecReportUtil;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import io.vertx.mutiny.core.eventbus.EventBus;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

@RegisterForReflection
@ApplicationScoped
public class SecurityScanner {

  static final Logger LOGGER = Logger.getLogger(SecurityScanner.class.getName());

  EventBus eventBus;
  CliCommandProcessor commandProcessor;
  ObjectMapper mapper = new ObjectMapper();

  public SecurityScanner(EventBus eventBus, CliCommandProcessor commandProcessor) {
    this.eventBus = eventBus;
    this.commandProcessor = commandProcessor;
  }

  @Blocking
  @ConsumeEvent("module.extract.finished")
  public String scanModule(FormData archive) {
    LOGGER.info(String.format("Starting scan for module %s, version %s",
            archive.getModule().getName(),
            archive.getModule().getCurrentVersion()
    ));
    File workingDirectory = archive.getCompressedModule().getParentFile();
    String output = commandProcessor.runCommand(
            workingDirectory,
            "sh", "-c", "tfsec -f json  --ignore-hcl-errors .");
    TfSecReport tfSecReport;
    try {
      tfSecReport = mapper.readValue(output, TfSecReport.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    Map<String, List<TfSecReport.TfSecResult>> securityReport = TfSecReportUtil
            .sanitizeAndGroupAndSortFindings(tfSecReport, workingDirectory.toPath());
    Report report = new Report(
            archive.getModule().getNamespace(),
            archive.getModule().getName(),
            archive.getModule().getProvider(),
            archive.getModule().getCurrentVersion(),
            securityReport
    );
    eventBus.requestAndForget("module.report.finished", report);
    LOGGER.info(String.format("Finished scan for module %s, version %s",
            archive.getModule().getName(),
            archive.getModule().getCurrentVersion()
    ));
    eventBus.requestAndForget("module.processing.finished", archive);
    return "ok";
  }
}
