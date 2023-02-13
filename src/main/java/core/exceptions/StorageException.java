package core.exceptions;

public class StorageException extends TapirException implements SevereException {

  public StorageException(String id) {
    super(String.format("Module/ Provider with id %s could not be found", id));
  }

  public StorageException(String moduleId, Throwable throwable) {
    super(
            String.format("Module/ Provider with id %s could not be found", moduleId),
            throwable);
  }
}

