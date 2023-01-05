package api.exceptions;

import api.exceptions.response.ErrorResponse;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationMapper implements ExceptionMapper<ConstraintViolationException> {
  @Override
  public Response toResponse(ConstraintViolationException e) {
    List<ErrorResponse.ErrorMessage> errorMessages = e.getConstraintViolations().stream()
            .map(constraintViolation -> new ErrorResponse.ErrorMessage(
                    constraintViolation.getPropertyPath().toString(),
                    constraintViolation.getMessage())
            )
            .collect(Collectors.toList());
    return Response.status(Response.Status.BAD_REQUEST)
            .entity(new ErrorResponse(errorMessages)).build();
  }
}
