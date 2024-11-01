package core.exceptions;

public class InvalidVersionException extends TapirException implements RegistryComplianceException {

  private static final String errorMessage = "Version %s is invalid and does not comply with the Terraform registry versioning specification";

  public InvalidVersionException(String version) {
    super(String.format(errorMessage, version));
  }

  public InvalidVersionException(String version, Throwable cause) {
    super(String.format(errorMessage, version), cause);
  }
}
