package core.terraform.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.terraform.Login;

public class ServiceDiscovery {

  @JsonProperty("modules.v1")
  private final String moduleV1 = "/terraform/modules/v1/";

  @JsonProperty("login.v1")
  private final Login loginv1 = new Login();
}
