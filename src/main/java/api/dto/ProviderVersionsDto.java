package api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import core.terraform.ProviderPlatform;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderVersionsDto {

  String version;
  List<String> protocols;
  List<ProviderPlatform> platforms;

  public ProviderVersionsDto(String version, List<ProviderPlatform> platforms) {
    this.version = version;
    this.platforms = platforms;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public List<String> getProtocols() {
    return protocols;
  }

  public void setProtocols(List<String> protocols) {
    this.protocols = protocols;
  }

  public List<ProviderPlatform> getPlatforms() {
    return platforms;
  }

  public void setPlatforms(List<ProviderPlatform> platforms) {
    this.platforms = platforms;
  }
}
