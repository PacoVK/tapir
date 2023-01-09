package api.mapper.exceptions;

import api.mapper.exceptions.response.ErrorResponse;
import core.exceptions.NotFoundException;
import core.exceptions.TapirException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TapirExceptionMapper implements ExceptionMapper<TapirException> {
  @Override
  public Response toResponse(TapirException e) {
    ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage(e.getMessage());
    Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
    if (e instanceof NotFoundException) {
      status = Response.Status.NOT_FOUND;
    }
    return Response.status(status)
            .entity(new ErrorResponse(errorMessage)).build();
  }
}
