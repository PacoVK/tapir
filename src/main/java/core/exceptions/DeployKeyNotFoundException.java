package core.exceptions;

public class DeployKeyNotFoundException extends TapirException implements NotFoundException {
  public DeployKeyNotFoundException(String id) {
    super(String.format("DeployKey for %s does not exists", id));
  }

  public DeployKeyNotFoundException(String id, Throwable cause) {
    super(
        String.format("DeployKey for %s does not exists", id),
        cause);
  }
}
