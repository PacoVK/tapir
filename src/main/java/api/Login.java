package api;

import core.auth.TapirLoginHandler;
import jakarta.enterprise.inject.Instance;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Produces(MediaType.APPLICATION_JSON)
@Path("/login")
public class Login {

  TapirLoginHandler loginHandler;

  public Login(Instance<TapirLoginHandler> loginHandlerInstance) {
    this.loginHandler = loginHandlerInstance.get();
  }

  @Path("/")
  @GET
  public Response verify(
         @QueryParam("apiKey") String apiKey
  ) {
    if (loginHandler.verifyToken(apiKey)) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

  @Path("/auth")
  @GET
  public Response auth(
          @QueryParam("client_id") String clientId,
          @QueryParam("code_challenge") String codeChallenge,
          @QueryParam("code_challenge_method") String codeChallengeMethod,
          @QueryParam("redirect_uri") String redirectUri,
          @QueryParam("response_type") String responseType,
          @QueryParam("state") String state
  ) throws URISyntaxException {
    URI redirectUrl = loginHandler.buildAuthorizeRedirectUrl(redirectUri, state, clientId);
    return Response.status(Response.Status.FOUND).location(redirectUrl).build();
  }

  @Path("/token")
  @POST
  @Consumes("application/x-www-form-urlencoded")
  public Response token(
      @FormParam("client_id") String clientId,
      @FormParam("redirect_uri") String redirectUri,
      @FormParam("code") String code
  ) throws Exception {
    URI redirectUrl = loginHandler.buildTokenRedirectUrl(redirectUri, code, clientId);
    return Response.status(Response.Status.TEMPORARY_REDIRECT).location(redirectUrl).build();
  }
}
