package core.exceptions;

public class InvalidConfigurationException extends TapirException implements SevereException {

  public InvalidConfigurationException(String id) {
    super(String.format("Configuration for %s is invalid", id));
  }

  public InvalidConfigurationException(String id, Throwable throwable) {
    super(
            String.format("Configuration for %s is invalid", id),
            throwable);
  }
}

