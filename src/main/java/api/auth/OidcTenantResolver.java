package api.auth;

import io.quarkus.oidc.TenantResolver;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OidcTenantResolver implements TenantResolver {

  @Override
  public String resolve(RoutingContext context) {
    String auth = context.request().getHeader("Authorization");
    if (auth != null && auth.toLowerCase().startsWith("bearer ")) {
      return "cli";
    }
    return null;
  }
}
