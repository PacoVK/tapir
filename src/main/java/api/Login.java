package api;

import io.quarkus.oidc.OidcConfigurationMetadata;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.ConfigProvider;

@Produces(MediaType.APPLICATION_JSON)
@Path("/login")
public class Login {

  @Inject
  OidcConfigurationMetadata configMetadata;

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
    String encode = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
    URI redirect = new URI(
            configMetadata.getAuthorizationUri()
            + "?redirect_uri=" + encode
            + "&state=" + state
            + "&response_type=code"
            + "&client_id=" + clientId
    );
    return Response.status(Response.Status.FOUND).location(redirect).build();
  }

  @Path("/token")
  @POST
  @Consumes("application/x-www-form-urlencoded")
  public Response token(
      @FormParam("client_id") String clientId,
      @FormParam("redirect_uri") String redirectUri,
      @FormParam("code") String code
  ) throws URISyntaxException {
    String clientSecret = ConfigProvider.getConfig()
            .getOptionalValue("quarkus.oidc.credentials.secret", String.class).orElse(null);
    String encode = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
    URI redirect = new URI(
            configMetadata.getTokenUri()
                    + "?client_id=" + clientId
            + "&code=" + code
                    + "&redirect_uri=" + encode
            + (clientSecret != null ? "&client_secret=" + clientSecret : "")
    );
    return Response.status(Response.Status.TEMPORARY_REDIRECT).location(redirect).build();
  }
}
