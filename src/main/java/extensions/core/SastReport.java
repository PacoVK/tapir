package extensions.core;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SastReport {

  public SastReport() {}

  public SastReport(String moduleNamespace, String moduleName, String provider, String moduleVersion, AbstractSastReport report) {
    this.moduleNamespace = moduleNamespace;
    this.moduleName = moduleName;
    this.provider = provider;
    this.moduleVersion = moduleVersion;
    this.report = report;
  }

  private String id;
  private String moduleName;
  private String moduleVersion;
  private String moduleNamespace;
  private String provider;
  private AbstractSastReport report;

  public String getId() {
    return String.format("%s-%s-%s-%s", getModuleNamespace(), getModuleName(), getProvider(), getModuleVersion());
  }

  public void setId(String id) {
    this.id = id;
  }

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

  public AbstractSastReport getReport() {
    return report;
  }

  public void setReport(AbstractSastReport report) {
    this.report = report;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }
}
