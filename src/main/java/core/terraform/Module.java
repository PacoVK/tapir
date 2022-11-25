package core.terraform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Module {

  public Module() {}

  public Module(String namespace, String name, String provider) {
    this.namespace = namespace;
    this.name = name;
    this.provider = provider;
  }

  public Module(String namespace, String name, String provider, String version) {
    this.namespace = namespace;
    this.name = name;
    this.provider = provider;
    this.versions = new LinkedList<>(List.of(new ModuleVersion(version)));
  }

  private String id;
  private String owner;
  private String namespace;
  private String name;
  private LinkedList<ModuleVersion> versions;
  private String provider;
  private String description;
  private String source;
  private Instant published_at;
  private Integer downloads = 0;
  private Boolean verified;
  private Map<String, JsonObject> scanResults;

  @JsonProperty("reports")
  private void unpackNestedReports(Map<String,Map<String,JsonObject>> brand) {
    this.scanResults = brand.get("security");
  }

  @JsonIgnore
  public String getCurrentVersion(){
    return versions.getLast().getVersion();
  }

  public Map<String, JsonObject> getScanResults() {
    return scanResults;
  }
  public void setScanResults(Map<String, JsonObject> scanResults) {
    this.scanResults = scanResults;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
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

  public LinkedList<ModuleVersion> getVersions() {
    return versions;
  }

  public void setVersions(LinkedList<ModuleVersion> versions) {
    this.versions = versions;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
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

  public Boolean getVerified() {
    return verified;
  }

  public void setVerified(Boolean verified) {
    this.verified = verified;
  }

}


