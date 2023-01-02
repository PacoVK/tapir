package core.terraform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Set;
import java.util.TreeSet;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Module {

  public Module() {}

  public Module(String namespace, String name, String provider) {
    this.namespace = namespace;
    this.name = name;
    this.provider = provider;
    setId(computeId());
  }

  public Module(String namespace, String name, String provider, String version) {
    this.namespace = namespace;
    this.name = name;
    this.provider = provider;
    this.versions = new TreeSet<>(Set.of(new ModuleVersion(version)));
    setId(computeId());
  }

  private String id;
  private String namespace;
  private String name;
  private TreeSet<ModuleVersion> versions;
  private String provider;
  private Instant published_at;
  private Integer downloads = 0;

  @JsonIgnore
  public String getCurrentVersion() {
    return versions.first().getVersion();
  }

  private String computeId() {
    return String.format("%s-%s-%s", getNamespace(), getName(), getProvider());
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TreeSet<ModuleVersion> getVersions() {
    return versions;
  }

  public void setVersions(TreeSet<ModuleVersion> versions) {
    this.versions = versions;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public Instant getPublished_at() {
    return published_at;
  }

  public void setPublished_at(Instant published_at) {
    this.published_at = published_at;
  }

  public Integer getDownloads() {
    return downloads;
  }

  public void setDownloads(Integer downloads) {
    this.downloads = downloads;
  }

}


