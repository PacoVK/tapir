package core.terraform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.maven.artifact.versioning.ComparableVersion;

public class ModuleVersion implements Comparable<ModuleVersion> {
  public ModuleVersion() {}

  public ModuleVersion(String version) {
    this.version = version;
    this.comparableVersion = new ComparableVersion(version);
  }

  String version;

  @JsonIgnore
  ComparableVersion comparableVersion;

  public void setVersion(String version) {
    this.version = version;
  }

  public String getVersion() {
    return version;
  }

  public ComparableVersion getComparableVersion() {
    return comparableVersion;
  }

  @Override
  public int compareTo(ModuleVersion o) {
    return o.comparableVersion.compareTo(this.comparableVersion);
  }
}
