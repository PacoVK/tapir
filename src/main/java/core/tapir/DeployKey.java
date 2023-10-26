package core.tapir;

import java.time.Instant;

public class DeployKey extends CoreEntity {

  String id;
  String key;
  Instant lastModifiedAt;

  public DeployKey() {}

  public DeployKey(String id, String key) {
    this.id = id;
    this.key = key;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Instant getLastModifiedAt() {
    return lastModifiedAt;
  }

  public void setLastModifiedAt(Instant lastModifiedAt) {
    this.lastModifiedAt = lastModifiedAt;
  }
}
