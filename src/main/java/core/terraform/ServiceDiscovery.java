package core.terraform;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceDiscovery {

  @JsonProperty("modules.v1")
  private String moduleV1 = "/terraform/modules/v1/";

  @JsonProperty("providers.v1")
  private String providersV1 = "/terraform/providers/v1/";

  @JsonProperty("login.v1")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private LoginDiscovery loginV1;

  public void setLoginV1(LoginDiscovery loginV1) {
    this.loginV1 = loginV1;
  }
}
