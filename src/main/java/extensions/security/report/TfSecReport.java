package extensions.security.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TfSecReport {

  private List<TfSecResult> results;

  public List<TfSecResult> getResults() {
    return results;
  }

  public void setResults(List<TfSecResult> results) {
    this.results = results;
  }

  public static class TfSecResult {
    private String rule_id;
    private String long_id;
    private String rule_description;
    private String rule_provider;
    private String rule_service;
    private String impact;
    private String resolution;
    private List<String> links;
    private String description;
    private Severity severity;
    private Boolean warning;
    private Integer status;
    private String resource;
    private Location location;

    public String getRule_id() {
      return rule_id;
    }

    public void setRule_id(String rule_id) {
      this.rule_id = rule_id;
    }

    public String getLong_id() {
      return long_id;
    }

    public void setLong_id(String long_id) {
      this.long_id = long_id;
    }

    public String getRule_description() {
      return rule_description;
    }

    public void setRule_description(String rule_description) {
      this.rule_description = rule_description;
    }

    public String getRule_provider() {
      return rule_provider;
    }

    public void setRule_provider(String rule_provider) {
      this.rule_provider = rule_provider;
    }

    public String getRule_service() {
      return rule_service;
    }

    public void setRule_service(String rule_service) {
      this.rule_service = rule_service;
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

    @JsonDeserialize(using = LocationDeserializer.class)
    public static class Location {
      private String filename;
      private Integer start_line;
      private Integer end_line;

      public Location() {
      }

      public Location(String filename, Integer start_line, Integer end_line) {
        this.filename = filename;
        this.start_line = start_line;
        this.end_line = end_line;
      }

      public String getFilename() {
        return filename;
      }

      public void setFilename(String filename) {
        this.filename = filename;
      }

      public Integer getStart_line() {
        return start_line;
      }

      public void setStart_line(Integer start_line) {
        this.start_line = start_line;
      }

      public Integer getEnd_line() {
        return end_line;
      }

      public void setEnd_line(Integer end_line) {
        this.end_line = end_line;
      }
    }
  }
}
