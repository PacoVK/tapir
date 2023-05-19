package api.mapper.exceptions;

import api.mapper.exceptions.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationMapper implements ExceptionMapper<ConstraintViolationException> {

  static final Logger LOGGER = Logger.getLogger(ConstraintViolationMapper.class.getName());

  @Override
  public Response toResponse(ConstraintViolationException e) {
    String errorId = UUID.randomUUID().toString();
    LOGGER.log(Level.SEVERE, "errorId {0}: {1}", new Object[]{ errorId, e });
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
