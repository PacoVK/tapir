package core.exceptions;

public class TapirException extends Exception {

  public TapirException(String message) {
    super(message);
  }

  public TapirException(String message, Throwable cause) {
    super(message, cause);
  }
}
