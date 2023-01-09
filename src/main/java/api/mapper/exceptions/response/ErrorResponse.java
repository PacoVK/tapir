package api.mapper.exceptions.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Objects;

public class ErrorResponse {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String errorId;
  private List<ErrorMessage> errors;

  public ErrorResponse(String errorId, ErrorMessage errorMessage) {
    this.errorId = errorId;
    this.errors = List.of(errorMessage);
  }

  public ErrorResponse(ErrorMessage errorMessage) {
    this(null, errorMessage);
  }

  public ErrorResponse(List<ErrorMessage> errors) {
    this.errorId = null;
    this.errors = errors;
  }

  public ErrorResponse() {
  }

  public String getErrorId() {
    return errorId;
  }

  public void setErrorId(String errorId) {
    this.errorId = errorId;
  }

  public List<ErrorMessage> getErrors() {
    return errors;
  }

  public void setErrors(List<ErrorMessage> errors) {
    this.errors = errors;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ErrorResponse that = (ErrorResponse) o;

    if (!Objects.equals(errorId, that.errorId)) {
      return false;
    }
    return errors.equals(that.errors);
  }

  @Override
  public int hashCode() {
    int result = errorId != null ? errorId.hashCode() : 0;
    result = 31 * result + errors.hashCode();
    return result;
  }

  public static class ErrorMessage {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String path;
    private String message;

    public ErrorMessage(String path, String message) {
      this.path = path;
      this.message = message;
    }

    public ErrorMessage(String message) {
      this.path = null;
      this.message = message;
    }

    public ErrorMessage() {
    }

    public String getPath() {
      return path;
    }

    public String getMessage() {
      return message;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      ErrorMessage that = (ErrorMessage) o;

      if (!Objects.equals(path, that.path)) {
        return false;
      }
      return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
      int result = path != null ? path.hashCode() : 0;
      result = 31 * result + (message != null ? message.hashCode() : 0);
      return result;
    }
  }
}