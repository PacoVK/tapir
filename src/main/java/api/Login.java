package api;

import core.auth.TapirLoginHandler;
import java.net.URI;
import java.net.URISyntaxException;
import javax.enterprise.inject.Instance;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Path("/login")
public class Login {

  TapirLoginHandler loginHandler;

  public Login(Instance<TapirLoginHandler> loginHandlerInstance) {
    this.loginHandler = loginHandlerInstance.get();
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
