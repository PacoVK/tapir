package api.mapper.exceptions;

import core.exceptions.InvalidVersionException;
import static org.junit.jupiter.api.Assertions.assertEquals;

import api.mapper.exceptions.response.ErrorResponse;
import core.exceptions.ModuleNotFoundException;
import core.exceptions.ReportNotFoundException;
import core.exceptions.StorageException;
import core.exceptions.TapirException;
import java.util.List;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class TapirExceptionMapperTest {

  TapirExceptionMapper mapper = new TapirExceptionMapper();

  @Test
  void getCorrectStatusWhenModuleNotFoundExceptionOccurs() {
    TapirException notFoundException = new ModuleNotFoundException("fake-id-version");
    Response notFoundResponse = mapper.toResponse(notFoundException);
    List<ErrorResponse.ErrorMessage> errors = ((ErrorResponse) notFoundResponse.getEntity()).getErrors();
    assertEquals(notFoundResponse.getStatus(), 404);
    assertEquals(errors.size(), 1);
    assertEquals(errors.get(0).getMessage(), "Module with id fake-id-version could not be found");
  }

  @Test
  void getCorrectStatusWhenReportNotFoundExceptionOccurs() {
    TapirException notFoundException = new ReportNotFoundException("fake-id-version");
    Response notFoundResponse = mapper.toResponse(notFoundException);
    List<ErrorResponse.ErrorMessage> errors = ((ErrorResponse) notFoundResponse.getEntity()).getErrors();
    assertEquals(notFoundResponse.getStatus(), 404);
    assertEquals(errors.size(), 1);
    assertEquals(errors.get(0).getMessage(), "Report for module with id fake-id-version could not be found");
  }

  @Test
  void getCorrectStatusWhenRuntimeExceptionOccurs() {
    TapirException runtimeException = new StorageException("fake-id-version");
    Response notFoundResponse = mapper.toResponse(runtimeException);
    List<ErrorResponse.ErrorMessage> errors = ((ErrorResponse) notFoundResponse.getEntity()).getErrors();
    assertEquals(notFoundResponse.getStatus(), 500);
    assertEquals(errors.size(), 1);
    assertEquals(errors.get(0).getMessage(), "Module/ Provider with id fake-id-version could not be found");
  }

  @Test
  void getCorrectStatusWhenRegistryComplianceExceptionOccurs() {
    TapirException invalidVersionException = new InvalidVersionException("wrong-version");
    Response badRequestResponse = mapper.toResponse(invalidVersionException);
    List<ErrorResponse.ErrorMessage> errors = ((ErrorResponse) badRequestResponse.getEntity()).getErrors();
    assertEquals(badRequestResponse.getStatus(), 400);
    assertEquals(errors.size(), 1);
    assertEquals(errors.get(0).getMessage(), "Version wrong-version is invalid and does not comply with the Terraform registry versioning specification");
  }
}