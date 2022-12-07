package core.terraform;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Login {
  public Login() {
    String authServer = ConfigProvider.getConfig().getValue("quarkus.oidc.auth-server-url", String.class);
    String authorizationPath = ConfigProvider.getConfig().getValue("quarkus.oidc.authorization-path", String.class);
    String tokenPath = ConfigProvider.getConfig().getValue("quarkus.oidc.token-path", String.class);

    this.client = ConfigProvider.getConfig().getValue("quarkus.oidc.client-id", String.class);
    this.authz = new StringBuilder(authServer).append(authorizationPath).toString();
    this.token = new StringBuilder(authServer).append(tokenPath).toString();
  }
  private String client;
  private List<String> grant_types = List.of("authz_code");
  private String authz;
  private String token;
  private List<Integer> ports;

  public String getClient() {
    return client;
  }

  public void setClient(String client) {
    this.client = client;
  }

  public List<String> getGrant_types() {
    return grant_types;
  }

  public void setGrant_types(List<String> grant_types) {
    this.grant_types = grant_types;
  }

  public String getAuthz() {
    return authz;
  }

  public void setAuthz(String authz) {
    this.authz = authz;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public List<Integer> getPorts() {
    return ports;
  }

  public void setPorts(List<Integer> ports) {
    this.ports = ports;
  }
}
