package core.terraform;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.config.ConfigProvider;

public class ServiceDiscovery {

  private static ServiceDiscovery instance;

  ServiceDiscovery() {
    String clientId = ConfigProvider.getConfig().getValue("quarkus.oidc.client-id", String.class);
    this.loginV1 = Map.of(
            "client", clientId,
            "grant_types", List.of("authz_code"),
            "token", "/login/token",
            "authz", "/login/auth",
            "ports", List.of(10000, 10010)
    );
  }

  public static ServiceDiscovery getInstance() {
    if (instance == null) {
      instance = new ServiceDiscovery();
    }
    return instance;
  }

  @JsonProperty("modules.v1")
  private String moduleV1 = "/terraform/modules/v1/";

  @JsonProperty("providers.v1")
  private String providersV1 = "/terraform/providers/v1/";

  @JsonProperty("login.v1")
  private Map<String, Object> loginV1;
}
