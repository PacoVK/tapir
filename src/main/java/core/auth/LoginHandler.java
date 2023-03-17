package core.auth;

import java.net.URI;
import java.net.URISyntaxException;

public interface LoginHandler {

  URI buildAuthorizeRedirectUrl(String redirectToUri, String state, String clientId)
          throws URISyntaxException;

  URI buildTokenRedirectUrl(String redirectToUri, String code, String clientId)
          throws Exception;
}
