package api.exceptions;

import api.exceptions.response.ErrorResponse;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ThrowableMapper implements ExceptionMapper<Throwable> {

  static final Logger LOGGER = Logger.getLogger(ThrowableMapper.class.getName());

  @Override
  public Response toResponse(Throwable throwable) {
    String errorId = UUID.randomUUID().toString();
    LOGGER.log(Level.SEVERE, "errorId {0}: {1}", new Object[]{ errorId, throwable });
    String defaultErrorMessage = ResourceBundle
            .getBundle("ValidationMessages")
            .getString("System.error");
    ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage(defaultErrorMessage);
    ErrorResponse errorResponse = new ErrorResponse(errorId, errorMessage);
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
  }
}
