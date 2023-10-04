package extensions.security.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecurityFinding {

  private String id;
  private String qualifiedId;
  private String ruleDescription;
  private String provider;
  private String service;
  private String impact;
  private String resolution;
  private List<String> links;
  private String description;
  private Severity severity;
  private Boolean warning;
  private Integer status;
  private String resource;
  private Location location;

  public SecurityFinding(String id, String qualifiedId, String ruleDescription, String provider, String service, String impact, String resolution, List<String> links, String description, Severity severity, Boolean warning, Integer status, String resource, Location location) {
    this.id = id;
    this.qualifiedId = qualifiedId;
    this.ruleDescription = ruleDescription;
    this.provider = provider;
    this.service = service;
    this.impact = impact;
    this.resolution = resolution;
    this.links = links;
    this.description = description;
    this.severity = severity;
    this.warning = warning;
    this.status = status;
    this.resource = resource;
    this.location = location;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getQualifiedId() {
    return qualifiedId;
  }

  public void setQualifiedId(String qualifiedId) {
    this.qualifiedId = qualifiedId;
  }

  public String getRuleDescription() {
    return ruleDescription;
  }

  public void setRuleDescription(String ruleDescription) {
    this.ruleDescription = ruleDescription;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public String getImpact() {
    return impact;
  }

  public void setImpact(String impact) {
    this.impact = impact;
  }

  public String getResolution() {
    return resolution;
  }

  public void setResolution(String resolution) {
    this.resolution = resolution;
  }

  public List<String> getLinks() {
    return links;
  }

  public void setLinks(List<String> links) {
    this.links = links;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Severity getSeverity() {
    return severity;
  }

  public void setSeverity(Severity severity) {
    this.severity = severity;
  }

  public Boolean getWarning() {
    return warning;
  }

  public void setWarning(Boolean warning) {
    this.warning = warning;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public static class Location {
    private String fileName;
    private Integer startLine;
    private Integer endLine;

    public Location(String fileName, Integer startLine, Integer endLine) {
      this.fileName = fileName;
      this.startLine = startLine;
      this.endLine = endLine;
    }

    public String getFileName() {
      return fileName;
    }

    public void setFileName(String fileName) {
      this.fileName = fileName;
    }

    public Integer getStartLine() {
      return startLine;
    }

    public void setStartLine(Integer startLine) {
      this.startLine = startLine;
    }

    public Integer getEndLine() {
      return endLine;
    }

    public void setEndLine(Integer endLine) {
      this.endLine = endLine;
    }
  }
}
