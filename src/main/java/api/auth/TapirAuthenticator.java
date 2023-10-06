package api.auth;

import io.quarkus.oidc.runtime.OidcAuthenticationMechanism;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.smallrye.mutiny.Uni;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import java.util.Set;

@Alternative
@Priority(1)
@ApplicationScoped
class TapirAuthenticator implements HttpAuthenticationMechanism {

  OidcAuthenticationMechanism oidcAuthenticationMechanism;
  ApiKeyAuthenticationMechanism apiKeyAuthenticationMechanism;

  public TapirAuthenticator(
      OidcAuthenticationMechanism oidcAuthenticationMechanism,
      ApiKeyAuthenticationMechanism apiKeyAuthenticationMechanism) {
    this.oidcAuthenticationMechanism = oidcAuthenticationMechanism;
    this.apiKeyAuthenticationMechanism = apiKeyAuthenticationMechanism;
  }

  @Override
  public Uni<SecurityIdentity> authenticate(
      RoutingContext context,
      IdentityProviderManager identityProviderManager) {
    MultiMap headers = context.request().headers();
    if (headers.contains(ApiKeyAuthenticationMechanism.X_API_KEY_HEADER)) {
      return apiKeyAuthenticationMechanism.authenticate(context, identityProviderManager);
    }
    return oidcAuthenticationMechanism.authenticate(context, identityProviderManager);
  }

  @Override
  public Uni<ChallengeData> getChallenge(RoutingContext context) {
    MultiMap headers = context.request().headers();
    if (headers.contains(ApiKeyAuthenticationMechanism.X_API_KEY_HEADER)) {
      return apiKeyAuthenticationMechanism.getChallenge(context);
    }
    return oidcAuthenticationMechanism.getChallenge(context);
  }

  @Override
  public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
    return oidcAuthenticationMechanism.getCredentialTypes();
  }
}
