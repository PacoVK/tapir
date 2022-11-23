package core.terraform;

public class ModuleVersion {
  public ModuleVersion() {}

  ModuleVersion(String version) {
    this.version = version;
  }

  String version;

  public String getVersion() {
    return version;
  }
}
