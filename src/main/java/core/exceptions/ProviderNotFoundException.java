package core.exceptions;

public class ProviderNotFoundException extends TapirException implements NotFoundException {

  public ProviderNotFoundException(String id) {
    super(String.format("Provider with id %s could not be found", id));
  }

  public ProviderNotFoundException(String providerId, Throwable throwable) {
    super(
            String.format("Provider with id %s could not be found", providerId),
            throwable);
  }
}

