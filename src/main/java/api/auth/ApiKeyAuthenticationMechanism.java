package api.auth;

import core.exceptions.DeployKeyNotFoundException;
import core.exceptions.ModuleNotFoundException;
import core.service.DeployKeyService;
import core.service.ModuleService;
import core.service.ProviderService;
import core.tapir.DeployKey;
import core.terraform.Module;
import core.terraform.Provider;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.quarkus.vertx.http.runtime.security.HttpCredentialTransport;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

@ApplicationScoped
public class ApiKeyAuthenticationMechanism implements HttpAuthenticationMechanism {

  static final Logger LOGGER = Logger.getLogger(ApiKeyAuthenticationMechanism.class.getName());

  static final String X_API_KEY_HEADER = "x-api-key";
  protected static final ChallengeData UNAUTHORIZED_CHALLENGE = new ChallengeData(
      HttpResponseStatus.UNAUTHORIZED.code(),
      HttpHeaderNames.WWW_AUTHENTICATE, X_API_KEY_HEADER);
  private static final HttpCredentialTransport OIDC_SERVICE_TRANSPORT = new HttpCredentialTransport(
      HttpCredentialTransport.Type.OTHER_HEADER, X_API_KEY_HEADER);

  DeployKeyService deployKeyService;
  ModuleService moduleService;
  ProviderService providerService;

  public ApiKeyAuthenticationMechanism(DeployKeyService deployKeyService, ModuleService moduleService, ProviderService providerService) {
    this.deployKeyService = deployKeyService;
    this.moduleService = moduleService;
    this.providerService = providerService;
  }

  @Override
  public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
    final String apiKeyHeader = context.request().getHeader(X_API_KEY_HEADER);
    try {
      DeployKey deployKey = fetchDeployKeyByValue(apiKeyHeader);
      if (this.validateKeyByRequestPath(deployKey, context.request().path())) {
        return tapirSecurityIdentity();
      }
    } catch (DeployKeyNotFoundException e) {
      LOGGER.warning(e.getMessage());
      throw new AuthenticationFailedException(e.getMessage());
    } catch (Exception e) {
      LOGGER.fine(e.getMessage());
      throw new AuthenticationFailedException(e.getMessage());
    }
    return Uni.createFrom().nullItem();
  }

  @Override
  public Uni<ChallengeData> getChallenge(RoutingContext context) {
    return Uni.createFrom().item(UNAUTHORIZED_CHALLENGE);
  }

  @Override
  public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
    return Collections.singleton(TapirApiKeyAuthenticationRequest.class);
  }

  @Override
  public Uni<HttpCredentialTransport> getCredentialTransport(RoutingContext context) {
    return Uni.createFrom().item(OIDC_SERVICE_TRANSPORT);
  }

  private DeployKey fetchDeployKeyByValue(String apiKeyHeader) throws DeployKeyNotFoundException {
    return deployKeyService.getDeployKeyByValue(apiKeyHeader);
  }
  private boolean validateKeyByRequestPath(DeployKey key, String requestPath) {
    String resourceId = requestPath.split("/v1/")[1];
    if (requestPath.contains("modules")) {
      try {
        Module module = moduleService.getModule(resourceId);
        return key.ValidForModule(module);
      } catch (Exception e) {
        return false;
      }
    } else {
        Provider provider = null;
        try {
            provider = providerService.getProvider(resourceId);
        } catch (Exception e) {
          return false;
        }
        return key.ValidForProvider(provider);
    }
  }

  private Uni<SecurityIdentity> tapirSecurityIdentity() {
    return Uni.createFrom().item(
        QuarkusSecurityIdentity.builder()
            .setPrincipal(() -> "tapir")
            .addRole("publisher")
            .build()
    );
  }
}
