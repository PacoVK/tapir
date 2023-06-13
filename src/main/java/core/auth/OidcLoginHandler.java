package core.auth;

import io.quarkus.arc.lookup.LookupUnlessProperty;
import io.quarkus.oidc.OidcConfigurationMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import org.eclipse.microprofile.config.ConfigProvider;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
@LookupUnlessProperty(name = "registry.auth.enabled", stringValue = "false")
public class OidcLoginHandler extends TapirLoginHandler {

  OidcConfigurationMetadata configMetadata;

  public OidcLoginHandler(Instance<OidcConfigurationMetadata> configMetadataInstance) {
    this.configMetadata = configMetadataInstance.get();
  }

  @Override
  public Boolean verifyToken(String token) {
    return null;
  }

  public URI buildAuthorizeRedirectUrl(String redirectToUri, String state, String clientId)
          throws URISyntaxException {
    String encodedRedirectTo = URLEncoder.encode(redirectToUri, StandardCharsets.UTF_8);
    return new URI(
            configMetadata.getAuthorizationUri()
                    + "?redirect_uri=" + encodedRedirectTo
                    + "&state=" + state
                    + "&response_type=code"
                    + "&client_id=" + clientId
    );
  }

  public URI buildTokenRedirectUrl(String redirectToUri, String code, String clientId)
          throws URISyntaxException {
    String clientSecret = ConfigProvider.getConfig()
            .getOptionalValue("quarkus.oidc.credentials.secret", String.class).orElse(null);
    String encodedRedirectTo = URLEncoder.encode(redirectToUri, StandardCharsets.UTF_8);
    return new URI(
            configMetadata.getTokenUri()
                    + "?client_id=" + clientId
                    + "&code=" + code
                    + "&redirect_uri=" + encodedRedirectTo
                    + (clientSecret != null ? "&client_secret=" + clientSecret : "")
    );
  }
}
