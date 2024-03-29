package core.terraform;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceDiscovery {

  @JsonProperty("modules.v1")
  private String moduleV1 = "/terraform/modules/v1/";

  @JsonProperty("providers.v1")
  private String providersV1 = "/terraform/providers/v1/";
}
