package core.terraform;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record LoginDiscovery(
    String client,
    String authz,
    String token,
    @JsonProperty("grant_types") List<String> grantTypes,
    List<Integer> ports) {
}
