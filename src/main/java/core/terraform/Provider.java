package core.terraform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.TreeMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Provider extends CoreEntity {

  private String id;
  private String namespace;
  private String type;
  private TreeMap<ArtifactVersion, List<ProviderPlatform>> versions;
  private List<String> protocols;

  public Provider() {
  }

  public Provider(String namespace, String type) {
    this.namespace = namespace;
    this.type = type;
    this.versions = new TreeMap<>();
    setId(computeId());
  }

  @JsonIgnore
  public String getCurrentVersion() {
    return versions.firstEntry().getKey().getVersion();
  }

  private String computeId() {
    return String.format("%s-%s", getNamespace(), getType());
  }

  public String getId() {
    return computeId();
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public TreeMap<ArtifactVersion, List<ProviderPlatform>> getVersions() {
    return versions;
  }

  public void setVersions(TreeMap<ArtifactVersion, List<ProviderPlatform>> versions) {
    this.versions = versions;
  }

  public List<String> getProtocols() {
    return protocols;
  }

  public void setProtocols(List<String> protocols) {
    this.protocols = protocols;
  }

}
