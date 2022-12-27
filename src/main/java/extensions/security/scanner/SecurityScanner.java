package extensions.security.scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.service.upload.FormData;
import extensions.core.SastReport;
import extensions.security.report.TfSecReport;
import extensions.security.util.TfSecReportUtil;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

@RegisterForReflection
@ApplicationScoped
public class SecurityScanner {

  static final Logger LOGGER = Logger.getLogger(SecurityScanner.class.getName());

  EventBus eventBus;
  ObjectMapper mapper = new ObjectMapper();

  public SecurityScanner(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @ConsumeEvent("module.extract.finished")
  public void scanModule(FormData archive) {
    LOGGER.info(String.format("Starting scan for module %s, version %s",
            archive.getModule().getName(),
            archive.getModule().getCurrentVersion()
    ));
    File workingDirectory = archive.getCompressedModule().getParentFile();
    ProcessBuilder builder = new ProcessBuilder();
    builder.command("sh", "-c", "tfsec -f json  --ignore-hcl-errors .");
    builder.directory(workingDirectory);
    try {
      Process process = builder.start();
      StringBuilder responseStrBuilder = new StringBuilder();
      CommandOutputConsumer commandOutputConsumer =
              new CommandOutputConsumer(process.getInputStream(), responseStrBuilder::append);
      Future<?> future = Executors.newSingleThreadExecutor().submit(commandOutputConsumer);
      int exitCode = process.waitFor();
      assert exitCode == 0;
      future.get(10, TimeUnit.SECONDS);
      TfSecReport tfSecReport = mapper.readValue(responseStrBuilder.toString(), TfSecReport.class);
      Map<String, List<TfSecReport.TfSecResult>> securityReport = TfSecReportUtil
              .sanitizeAndGroupAndSortFindings(tfSecReport, workingDirectory.toPath());
      SastReport sastReport = new SastReport(
              archive.getModule().getNamespace(),
              archive.getModule().getName(),
              archive.getModule().getProvider(),
              archive.getModule().getCurrentVersion(),
              securityReport
      );
      eventBus.requestAndForget("module.report.finished", sastReport);
    } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
      throw new RuntimeException(e);
    }
    LOGGER.info(String.format("Finished scan for module %s, version %s",
            archive.getModule().getName(),
            archive.getModule().getCurrentVersion()
    ));
    eventBus.requestAndForget("module.processing.finished", archive);
  }

  private record CommandOutputConsumer(
          InputStream inputStream, Consumer<String> consumer
  ) implements Runnable {

    @Override
    public void run() {
      new BufferedReader(new InputStreamReader(inputStream)).lines()
              .forEach(consumer);
    }
  }
}
