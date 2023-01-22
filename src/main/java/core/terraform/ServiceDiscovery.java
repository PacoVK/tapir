package core.terraform;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceDiscovery {

  @JsonProperty("modules.v1")
  private final String moduleV1 = "/terraform/modules/v1/";
}
