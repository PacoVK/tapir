package core.terraform;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderPlatform {
  private String os;
  private String arch;
  private String shasum;
  private String fileName;

  public ProviderPlatform(String os, String arch) {
    this.os = os;
    this.arch = arch;
  }

  public ProviderPlatform(String os, String arch, String shasum, String fileName) {
    this.os = os;
    this.arch = arch;
    this.shasum = shasum;
    this.fileName = fileName;
  }

  ProviderPlatform() {
  }

  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = os;
  }

  public String getArch() {
    return arch;
  }

  public void setArch(String arch) {
    this.arch = arch;
  }

  public String getShasum() {
    return shasum;
  }

  public void setShasum(String shasum) {
    this.shasum = shasum;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
}
