package extensions.security.scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.service.upload.FormData;
import extensions.core.SastReport;
import extensions.security.report.TfSecReport;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

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
    LOGGER.info(String.format("Starting scan for module %s, version %s", archive.getModule().getName(), archive.getModule().getCurrentVersion()));

    ProcessBuilder builder = new ProcessBuilder();
    builder.command("sh", "-c", "tfsec -f json  --ignore-hcl-errors .");
    builder.directory(archive.getCompressedModule().getParentFile());
    try {
      Process process = builder.start();
      StringBuilder responseStrBuilder = new StringBuilder();
      CommandOutputConsumer commandOutputConsumer =
              new CommandOutputConsumer(process.getInputStream(), responseStrBuilder::append);
      Future<?> future = Executors.newSingleThreadExecutor().submit(commandOutputConsumer);
      int exitCode = process.waitFor();
      assert exitCode == 0;
      future.get(10, TimeUnit.SECONDS);
      SastReport sastReport = new SastReport(
              archive.getModule().getNamespace(),
              archive.getModule().getName(),
              archive.getModule().getProvider(),
              archive.getModule().getCurrentVersion(),
              mapper.readValue(responseStrBuilder.toString(), TfSecReport.class)
      );
      eventBus.requestAndForget("module.report.finished", sastReport);
    } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
      throw new RuntimeException(e);
    }
    LOGGER.info(String.format("Finished scan for module %s, version %s", archive.getModule().getName(), archive.getModule().getCurrentVersion()));
    eventBus.requestAndForget("module.processing.finished", archive);
  }

  private record CommandOutputConsumer(InputStream inputStream, Consumer<String> consumer) implements Runnable {

    @Override
      public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines()
                .forEach(consumer);
      }
    }
}
