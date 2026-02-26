package api.auth;

import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.vertx.http.runtime.security.HttpSecurityPolicy;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class DownloadPolicy implements HttpSecurityPolicy {

  @ConfigProperty(name = "registry.auth.login.enabled", defaultValue = "false")
  boolean loginEnabled;

  @Override
  public Uni<CheckResult> checkPermission(
      RoutingContext request,
      Uni<SecurityIdentity> identity,
      AuthorizationRequestContext requestContext) {
    if (!loginEnabled) {
      return Uni.createFrom().item(CheckResult.PERMIT);
    }
    return identity.map(id -> {
      if (id.isAnonymous()) {
        return CheckResult.DENY;
      }
      return CheckResult.PERMIT;
    });
  }

  @Override
  public String name() {
    return "download";
  }
}
