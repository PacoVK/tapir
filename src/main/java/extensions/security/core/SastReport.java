package extensions.security.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SastReport {

  public SastReport() {}

  public SastReport(String moduleName, String moduleVersion, String moduleNamespace, String provider, Map<String, Object> report) {
    this.moduleName = moduleName;
    this.moduleVersion = moduleVersion;
    this.moduleNamespace = moduleNamespace;
    this.provider = provider;
    this.report = report;
  }

  private String moduleName;
  private String moduleVersion;
  private String moduleNamespace;
  private String provider;
  private Map<String, Object> report;

  public String getModuleName() {
    return moduleName;
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public String getModuleVersion() {
    return moduleVersion;
  }

  public void setModuleVersion(String moduleVersion) {
    this.moduleVersion = moduleVersion;
  }

  public String getModuleNamespace() {
    return moduleNamespace;
  }

  public void setModuleNamespace(String moduleNamespace) {
    this.moduleNamespace = moduleNamespace;
  }

  public Map<String, Object> getReport() {
    return report;
  }

  public void setReport(Map<String, Object> report) {
    this.report = report;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }
}
