package extensions.security.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import extensions.core.AbstractSastReport;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrivyReport extends AbstractSastReport {
  public Integer SchemaVersion;
  public String ArtifactName;
  public String ArtifactType;
  public List<ReportResult> Results;

  public Integer getSchemaVersion() {
    return SchemaVersion;
  }

  public void setSchemaVersion(Integer schemaVersion) {
    SchemaVersion = schemaVersion;
  }

  public String getArtifactName() {
    return ArtifactName;
  }

  public void setArtifactName(String artifactName) {
    ArtifactName = artifactName;
  }

  public String getArtifactType() {
    return ArtifactType;
  }

  public void setArtifactType(String artifactType) {
    ArtifactType = artifactType;
  }

  public List<ReportResult> getResults() {
    return Results;
  }

  public void setResults(List<ReportResult> results) {
    Results = results;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  static class ReportResult {
    public String Target;
    @JsonProperty("Class")
    public String Clazz;
    public String Type;
    public Map<String, Integer> MisconfSummary;

    public List<Misconfigurations> Misconfigurations;

    public String getClazz() {
      return Clazz;
    }

    public void setClazz(String clazz) {
      Clazz = clazz;
    }

    public String getTarget() {
      return Target;
    }

    public void setTarget(String target) {
      Target = target;
    }

    public String getType() {
      return Type;
    }

    public void setType(String type) {
      Type = type;
    }

    public Map<String, Integer> getMisconfSummary() {
      return MisconfSummary;
    }

    public void setMisconfSummary(Map<String, Integer> misconfSummary) {
      MisconfSummary = misconfSummary;
    }

    public List<ReportResult.Misconfigurations> getMisconfigurations() {
      return Misconfigurations;
    }

    public void setMisconfigurations(List<ReportResult.Misconfigurations> misconfigurations) {
      Misconfigurations = misconfigurations;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Misconfigurations {
      public String Type;
      public String ID;
      public String AVDID;
      public String Title;
      public String Description;
      public String Message;
      public String Query;
      public String Resolution;
      public String Severity;
      public String PrimaryURL;
      public List<String> References;
      public String Status;
      public CauseMetadata CauseMetadata;

      public String getType() {
        return Type;
      }

      public void setType(String type) {
        Type = type;
      }

      public String getID() {
        return ID;
      }

      public void setID(String ID) {
        this.ID = ID;
      }

      public String getAVDID() {
        return AVDID;
      }

      public void setAVDID(String AVDID) {
        this.AVDID = AVDID;
      }

      public String getTitle() {
        return Title;
      }

      public void setTitle(String title) {
        Title = title;
      }

      public String getDescription() {
        return Description;
      }

      public void setDescription(String description) {
        Description = description;
      }

      public String getMessage() {
        return Message;
      }

      public void setMessage(String message) {
        Message = message;
      }

      public String getQuery() {
        return Query;
      }

      public void setQuery(String query) {
        Query = query;
      }

      public String getResolution() {
        return Resolution;
      }

      public void setResolution(String resolution) {
        Resolution = resolution;
      }

      public String getSeverity() {
        return Severity;
      }

      public void setSeverity(String severity) {
        Severity = severity;
      }

      public String getPrimaryURL() {
        return PrimaryURL;
      }

      public void setPrimaryURL(String primaryURL) {
        PrimaryURL = primaryURL;
      }

      public List<String> getReferences() {
        return References;
      }

      public void setReferences(List<String> references) {
        References = references;
      }

      public String getStatus() {
        return Status;
      }

      public void setStatus(String status) {
        Status = status;
      }

      public Misconfigurations.CauseMetadata getCauseMetadata() {
        return CauseMetadata;
      }

      public void setCauseMetadata(Misconfigurations.CauseMetadata causeMetadata) {
        CauseMetadata = causeMetadata;
      }

      @JsonInclude(JsonInclude.Include.NON_NULL)
      @JsonIgnoreProperties(ignoreUnknown = true)
      static class CauseMetadata {
        public String Resource;
        public String Provider;
        public String Service;
        public String StartLine;
        public String EndLine;

        public Code code;

        public String getResource() {
          return Resource;
        }

        public void setResource(String resource) {
          Resource = resource;
        }

        public String getProvider() {
          return Provider;
        }

        public void setProvider(String provider) {
          Provider = provider;
        }

        public String getService() {
          return Service;
        }

        public void setService(String service) {
          Service = service;
        }

        public String getStartLine() {
          return StartLine;
        }

        public void setStartLine(String startLine) {
          StartLine = startLine;
        }

        public String getEndLine() {
          return EndLine;
        }

        public void setEndLine(String endLine) {
          EndLine = endLine;
        }

        public Code getCode() {
          return code;
        }

        public void setCode(Code code) {
          this.code = code;
        }
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Code {
          public List<Lines> Lines;

          public List<Code.Lines> getLines() {
            return Lines;
          }

          public void setLines(List<Code.Lines> lines) {
            Lines = lines;
          }
          @JsonInclude(JsonInclude.Include.NON_NULL)
          @JsonIgnoreProperties(ignoreUnknown = true)
          static class Lines {
            public Integer Number;
            public String Content;
            public String Annotation;
            public Boolean IsCause;
            public Boolean Truncated;
            public Boolean FirstCause;
            public Boolean LastCause;

            public Integer getNumber() {
              return Number;
            }

            public void setNumber(Integer number) {
              Number = number;
            }

            public String getContent() {
              return Content;
            }

            public void setContent(String content) {
              Content = content;
            }

            public String getAnnotation() {
              return Annotation;
            }

            public void setAnnotation(String annotation) {
              Annotation = annotation;
            }

            public Boolean getCause() {
              return IsCause;
            }

            public void setCause(Boolean cause) {
              IsCause = cause;
            }

            public Boolean getTruncated() {
              return Truncated;
            }

            public void setTruncated(Boolean truncated) {
              Truncated = truncated;
            }

            public Boolean getFirstCause() {
              return FirstCause;
            }

            public void setFirstCause(Boolean firstCause) {
              FirstCause = firstCause;
            }

            public Boolean getLastCause() {
              return LastCause;
            }

            public void setLastCause(Boolean lastCause) {
              LastCause = lastCause;
            }
          }
        }
      }
    }
  }
}
