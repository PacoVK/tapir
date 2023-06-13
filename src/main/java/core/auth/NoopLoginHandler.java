package core.auth;

import core.exceptions.TapirException;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;

import java.net.URI;
import java.net.URISyntaxException;

@ApplicationScoped
@LookupIfProperty(name = "registry.auth.enabled", stringValue = "false")
public class NoopLoginHandler extends TapirLoginHandler {

  @Override
  public Boolean verifyToken(String token) {
    return true;
  }

  @Override
  public URI buildAuthorizeRedirectUrl(String redirectToUri, String state, String clientId)
          throws URISyntaxException {
    return new URI(redirectToUri
            + "?state=" + state
            + "&code=noop"
    );
  }

  @Override
  public URI buildTokenRedirectUrl(String redirectToUri, String code, String clientId)
          throws TapirException {
    throw new TapirException("Authentication was not enabled on this Tapir instance");
  }
}
