package core.service;

import core.tapir.User;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthService {

  SecurityIdentity securityIdentity;

  public AuthService(SecurityIdentity securityIdentity) {
    this.securityIdentity = securityIdentity;
  }

  public User getCurrentUser() {
    return new User(
        securityIdentity.getPrincipal().getName(),
        securityIdentity.getAttribute("esmail"),
        securityIdentity.getAttribute("given_name"),
        securityIdentity.getAttribute("family_name"),
        securityIdentity.getAttribute("preferred_username"),
        securityIdentity.getRoles());
  }
}
