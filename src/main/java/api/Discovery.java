package api;

import core.terraform.LoginDiscovery;
import core.terraform.ServiceDiscovery;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Produces(MediaType.APPLICATION_JSON)
@Path("/.well-known")
public class Discovery {

  @ConfigProperty(name = "registry.auth.login.enabled", defaultValue = "false")
  boolean loginEnabled;

  @ConfigProperty(name = "registry.auth.login.client-id", defaultValue = "tapir-cli")
  String loginClientId;

  @ConfigProperty(name = "registry.auth.login.ports.min", defaultValue = "10000")
  int loginPortsMin;

  @ConfigProperty(name = "registry.auth.login.ports.max", defaultValue = "10010")
  int loginPortsMax;

  @ConfigProperty(name = "registry.auth.endpoint")
  String authEndpoint;

  @ConfigProperty(name = "registry.auth.auth-path")
  Optional<String> authPath;

  @ConfigProperty(name = "registry.auth.token-path")
  Optional<String> tokenPath;

  @GET
  @Path("/terraform.json")
  public Response getSupportedServices() {
    ServiceDiscovery serviceDiscovery = new ServiceDiscovery();
    if (loginEnabled) {
      String authzUrl = authEndpoint + authPath.orElse("/protocol/openid-connect/auth");
      String tokenUrl = authEndpoint + tokenPath.orElse("/protocol/openid-connect/token");
      List<Integer> ports = List.of(loginPortsMin, loginPortsMax);
      serviceDiscovery.setLoginV1(
          new LoginDiscovery(loginClientId, authzUrl, tokenUrl, List.of("authz_code"), ports)
      );
    }
    return Response.ok(serviceDiscovery).build();
  }
}
