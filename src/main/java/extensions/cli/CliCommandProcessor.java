package extensions.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CliCommandProcessor {

  public String runCommand(File workingDirectory, String... command) {
    StringBuilder commandOutputStrBuilder = new StringBuilder();
    ProcessBuilder builder = new ProcessBuilder();
    builder.command(command);
    builder.directory(workingDirectory);
    try {
      Process process = builder.start();

      CliCommandProcessor.CommandOutputConsumer commandOutputConsumer =
              new CliCommandProcessor.CommandOutputConsumer(
                      process.getInputStream(), commandOutputStrBuilder::append);
      Future<?> future = Executors.newSingleThreadExecutor().submit(commandOutputConsumer);
      int exitCode = process.waitFor();
      assert exitCode == 0;
      future.get(10, TimeUnit.SECONDS);
    } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
      throw new RuntimeException(e);
    }
    return commandOutputStrBuilder.toString();
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
