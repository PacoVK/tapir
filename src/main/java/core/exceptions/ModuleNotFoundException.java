package core.exceptions;

public class ModuleNotFoundException extends TapirException implements NotFoundException {

  public ModuleNotFoundException(String id) {
    super(String.format("Module with id %s could not be found", id));
  }

  public ModuleNotFoundException(String moduleId, Throwable throwable) {
    super(
            String.format("Module with id %s could not be found", moduleId),
            throwable);
  }
}

