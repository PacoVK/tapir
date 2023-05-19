package api.mapper.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import api.mapper.exceptions.response.ErrorResponse;
import java.util.List;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class ThrowableMapperTest {

  ThrowableMapper mapper = new ThrowableMapper();

  @Test
  void getCorrectStatusWhenRuntimeExceptionOccurs() {
    RuntimeException runtimeException = new RuntimeException("something-completely-wild");
    Response notFoundResponse = mapper.toResponse(runtimeException);
    List<ErrorResponse.ErrorMessage> errors = ((ErrorResponse) notFoundResponse.getEntity()).getErrors();
    assertEquals(notFoundResponse.getStatus(), 500);
    assertEquals(errors.size(), 1);
    assertEquals(errors.get(0).getMessage(), "An unexpected error has occurred. Please raise an issue if you think this is a bug.");
  }
}