package core.terraform;

import org.apache.maven.artifact.versioning.ComparableVersion;

public class ModuleVersion implements Comparable<ModuleVersion> {
  public ModuleVersion() {}

  public ModuleVersion(String version) {
    this.version = version;
  }

  String version;

  public void setVersion(String version) {
    this.version = version;
  }

  public String getVersion() {
    return version;
  }

  @Override
  public int compareTo(ModuleVersion o) {
    return new ComparableVersion(o.getVersion()).compareTo(new ComparableVersion(this.version));
  }
}
