package extensions.security.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrivyReport {

  @JsonProperty("Results")
  private List<TrivyResult> results;

  public List<TrivyResult> getResults() {
    return results;
  }

  public void setResults(List<TrivyResult> results) {
    this.results = results;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class TrivyResult {

    @JsonProperty("Target")
    private String target;

    @JsonProperty("MisconfSummary")
    private MisconfSummary misconfSummary;

    @JsonProperty("Misconfigurations")
    private List<Misconfigurations> misconfigurations;

    public String getTarget() {
      return target;
    }

    public void setTarget(String target) {
      this.target = target;
    }

    public MisconfSummary getMisconfSummary() {
      return misconfSummary;
    }

    public void setMisconfSummary(MisconfSummary misconfSummary) {
      this.misconfSummary = misconfSummary;
    }

    public List<Misconfigurations> getMisconfigurations() {
      return misconfigurations;
    }

    public void setMisconfigurations(List<Misconfigurations> misconfigurations) {
      this.misconfigurations = misconfigurations;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class MisconfSummary {

    @JsonProperty("Successes")
    Integer successes;

    @JsonProperty("Failures")
    Integer failures;

    @JsonProperty("Exceptions")
    Integer exceptions;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Misconfigurations implements Comparable<Misconfigurations> {

    @JsonProperty("ID")
    String id;

    @JsonProperty("Title")
    String title;

    @JsonProperty("Description")
    String description;

    @JsonProperty("Message")
    String message;

    @JsonProperty("Resolution")
    String resolution;

    @JsonProperty("Severity")
    Severity severity;

    @JsonProperty("References")
    List<String> references;

    @JsonProperty("PrimaryURL")
    String primaryUrl;

    @JsonProperty("CauseMetadata")
    CauseMetadata causeMetadata;

    @Override
    public int compareTo(Misconfigurations other) {
      return this.severity.compareTo(other.severity);
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getResolution() {
      return resolution;
    }

    public void setResolution(String resolution) {
      this.resolution = resolution;
    }

    public Severity getSeverity() {
      return severity;
    }

    public void setSeverity(Severity severity) {
      this.severity = severity;
    }

    public List<String> getReferences() {
      return references;
    }

    public void setReferences(List<String> references) {
      this.references = references;
    }

    public String getPrimaryUrl() {
      return primaryUrl;
    }

    public void setPrimaryUrl(String primaryUrl) {
      this.primaryUrl = primaryUrl;
    }

    public CauseMetadata getCauseMetadata() {
      return causeMetadata;
    }

    public void setCauseMetadata(CauseMetadata causeMetadata) {
      this.causeMetadata = causeMetadata;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CauseMetadata {

      @JsonProperty("Resource")
      String resource;

      @JsonProperty("Provider")
      String provider;

      @JsonProperty("Service")
      String service;

      @JsonProperty("StartLine")
      Integer startLine;

      @JsonProperty("EndLine")
      Integer endLine;

      public String getResource() {
        return resource;
      }

      public void setResource(String resource) {
        this.resource = resource;
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
}
