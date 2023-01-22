package extensions.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import extensions.docs.report.TerraformDocumentation;
import extensions.security.report.TfSecReport;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Report {

  public Report() {}

  public Report(String moduleNamespace, String moduleName,
                String provider, String moduleVersion
  ) {
    this.moduleNamespace = moduleNamespace;
    this.moduleName = moduleName;
    this.provider = provider;
    this.moduleVersion = moduleVersion;
  }

  private String id;
  private String moduleName;
  private String moduleVersion;
  private String moduleNamespace;
  private String provider;
  private Map<String, List<TfSecReport.TfSecResult>> securityReport;
  private TerraformDocumentation documentation;

  public String getId() {
    return String.format("%s-%s-%s-%s",
            getModuleNamespace(),
            getModuleName(),
            getProvider(),
            getModuleVersion()
    );
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

  public Map<String, List<TfSecReport.TfSecResult>> getSecurityReport() {
    return securityReport;
  }

  public void setSecurityReport(Map<String, List<TfSecReport.TfSecResult>> securityReport) {
    this.securityReport = securityReport;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public TerraformDocumentation getDocumentation() {
    return documentation;
  }

  public void setDocumentation(TerraformDocumentation documentation) {
    this.documentation = documentation;
  }
}
