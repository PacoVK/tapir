package core.exceptions;

public class PlatformNotFoundException extends TapirException implements NotFoundException {
  public PlatformNotFoundException(String version) {
    super(String.format("Platform for version %s could not be found", version));
  }

  public PlatformNotFoundException(String version, Throwable cause) {
    super(String.format("Platform for version %s could not be found", version), cause);
  }
}
