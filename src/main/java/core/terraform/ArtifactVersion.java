package core.terraform;

import org.apache.maven.artifact.versioning.ComparableVersion;

public class ArtifactVersion implements Comparable<ArtifactVersion> {
  public ArtifactVersion() {}

  public ArtifactVersion(String version) {
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
  public int compareTo(ArtifactVersion o) {
    return new ComparableVersion(o.getVersion()).compareTo(new ComparableVersion(this.version));
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof ArtifactVersion && compareTo((ArtifactVersion) o) == 0;
  }

  @Override
  public int hashCode() {
    return version.hashCode();
  }

  @Override
  public String toString() {
    return getVersion();
  }
}
