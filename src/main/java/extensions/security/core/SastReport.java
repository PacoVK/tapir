package extensions.security.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import core.terraform.Module;
import io.vertx.core.json.JsonObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SastReport {

  public SastReport() {}

  public SastReport(String moduleName, String moduleVersion, String moduleNamespace, JsonObject report) {
    this.moduleName = moduleName;
    this.moduleVersion = moduleVersion;
    this.moduleNamespace = moduleNamespace;
    this.report = report;
  }

  private String moduleName;
  private String moduleVersion;
  private String moduleNamespace;
  private JsonObject report;

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

  public JsonObject getReport() {
    return report;
  }

  public void setReport(JsonObject report) {
    this.report = report;
  }
}
