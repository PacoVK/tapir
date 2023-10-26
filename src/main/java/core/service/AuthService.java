package core.service;

import core.tapir.User;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

@ApplicationScoped
public class AuthService {

  static final String TOKEN_EMAIL_ATTRIBUTE_CONFIG =
      "registry.auth.token-attributes.email";
  static final String TOKEN_GIVEN_NAME_ATTRIBUTE_CONFIG =
      "registry.auth.token-attributes.given_name";
  static final String TOKEN_FAMILY_NAME_ATTRIBUTE_CONFIG =
      "registry.auth.token-attributes.family_name";
  static final String TOKEN_PREFERRED_USERNAME_ATTRIBUTE_CONFIG =
      "registry.auth.token-attributes.preferred_username";

  SecurityIdentity securityIdentity;
  Config config;

  public AuthService(SecurityIdentity securityIdentity) {
    this.securityIdentity = securityIdentity;
    this.config = ConfigProvider.getConfig();
  }

  public User getCurrentUser() {
    return new User(
        securityIdentity.getPrincipal().getName(),
        securityIdentity.getAttribute(
            config.getConfigValue(TOKEN_EMAIL_ATTRIBUTE_CONFIG).getValue()
        ),
        securityIdentity.getAttribute(TOKEN_GIVEN_NAME_ATTRIBUTE_CONFIG),
        securityIdentity.getAttribute(TOKEN_FAMILY_NAME_ATTRIBUTE_CONFIG),
        securityIdentity.getAttribute(TOKEN_PREFERRED_USERNAME_ATTRIBUTE_CONFIG),
        securityIdentity.getRoles());
  }
}
