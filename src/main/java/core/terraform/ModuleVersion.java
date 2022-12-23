package core.terraform;

public class ModuleVersion {
  public ModuleVersion() {}

  public ModuleVersion(String version) {
    this.version = version;
  }

  String version;

  public String getVersion() {
    return version;
  }
}
