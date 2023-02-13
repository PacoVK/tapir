package api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class GpgPublicKey {
  String source = "Tapir";
  @JsonProperty("key_id")
  String keyId;
  @JsonProperty("ascii_armor")
  String asciiAmor;

  public GpgPublicKey() {
  }

  public GpgPublicKey(String keyId, String asciiAmor) {
    this.keyId = keyId;
    this.asciiAmor = asciiAmor;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getKeyId() {
    return keyId;
  }

  public void setKeyId(String keyId) {
    this.keyId = keyId;
  }

  public String getAsciiAmor() {
    return asciiAmor;
  }

  public void setAsciiAmor(String asciiAmor) {
    this.asciiAmor = asciiAmor;
  }
}
