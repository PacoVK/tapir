package core.exceptions;

public class ReportNotFoundException extends TapirException implements NotFoundException {

  private static final String errorMessage = "Report for module with id %s could not be found";

  public ReportNotFoundException(String id) {
    super(String.format(errorMessage, id));
  }

  public ReportNotFoundException(String moduleId, Throwable throwable) {
    super(
            String.format("Report for module with id %s could not be found", moduleId),
            throwable);
  }
}

