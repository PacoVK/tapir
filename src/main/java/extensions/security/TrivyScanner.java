package extensions.security;

import core.service.upload.FormData;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

@ApplicationScoped
public class TrivyScanner {

  static final Logger LOGGER = Logger.getLogger(TrivyScanner.class.getName());

  EventBus eventBus;

  public TrivyScanner(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @ConsumeEvent("module.scan")
  public void scanModule(FormData archive) {
    LOGGER.info(String.format("Starting scan for module %s, version %s", archive.getModule().getName(), archive.getModule().getCurrentVersion()));
    ProcessBuilder builder = new ProcessBuilder();
    builder.command("sh", "-c", "trivy config -f json .");
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
      archive.getModule().setScanResults(Map.of(archive.getModule().getCurrentVersion(), new JsonObject(responseStrBuilder.toString())));
    } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
      throw new RuntimeException(e);
    }
    LOGGER.info(String.format("Finished scan for module %s, version %s", archive.getModule().getName(), archive.getModule().getCurrentVersion()));
    eventBus.publish("publish.result", archive);
  }

  private record CommandOutputConsumer(InputStream inputStream, Consumer<String> consumer) implements Runnable {

    @Override
      public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines()
                .forEach(consumer);
      }
    }
}
