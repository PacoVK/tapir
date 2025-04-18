package api.mapper.exceptions;

import api.mapper.exceptions.response.ErrorResponse;
import core.exceptions.NotFoundException;
import core.exceptions.RegistryComplianceException;
import core.exceptions.TapirException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class TapirExceptionMapper implements ExceptionMapper<TapirException> {

  static final Logger LOGGER = Logger.getLogger(TapirExceptionMapper.class.getName());

  @Override
  public Response toResponse(TapirException e) {
    String errorId = UUID.randomUUID().toString();
    LOGGER.log(Level.SEVERE, "errorId {0}: {1}", new Object[]{ errorId, e});
    LOGGER.log(Level.SEVERE, "cause: {0}", new Object[]{ e.getCause()});
    ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage(e.getMessage());
    Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
    if (e instanceof NotFoundException) {
      status = Response.Status.NOT_FOUND;
    }
    if(e instanceof RegistryComplianceException) {
      status = Response.Status.BAD_REQUEST;
    }
    return Response.status(status)
            .entity(new ErrorResponse(errorId, errorMessage))
            .header("Content-Type", MediaType.APPLICATION_JSON).build();
  }
}
