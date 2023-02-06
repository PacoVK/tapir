package api.dto;

import api.dto.serializer.GpgPublicKeySerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderTerraformDto {

  public ProviderTerraformDto(String namespace, String type) {
    this.namespace = namespace;
    this.type = type;
  }

  private String namespace;
  private String type;
  private String os;
  private String arch;
  private String version;
  private List<String> protocols;
  private String filename;
  private String download_url;
  private String shasums_url;
  private String shasums_signature_url;
  private String shasum;
  @JsonSerialize(using = GpgPublicKeySerializer.class)
  private List<GpgPublicKey> signing_keys;

  public String getNamespace() {
    return namespace;
  }

  public String getType() {
    return type;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
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

  public List<String> getProtocols() {
    return protocols;
  }

  public void setProtocols(List<String> protocols) {
    this.protocols = protocols;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getDownload_url() {
    return download_url;
  }

  public void setDownload_url(String download_url) {
    this.download_url = download_url;
  }

  public String getShasums_url() {
    return shasums_url;
  }

  public void setShasums_url(String shasums_url) {
    this.shasums_url = shasums_url;
  }

  public String getShasums_signature_url() {
    return shasums_signature_url;
  }

  public void setShasums_signature_url(String shasums_signature_url) {
    this.shasums_signature_url = shasums_signature_url;
  }

  public String getShasum() {
    return shasum;
  }

  public void setShasum(String shasum) {
    this.shasum = shasum;
  }

  public List<GpgPublicKey> getSigning_keys() {
    return signing_keys;
  }

  public void setSigning_keys(List<GpgPublicKey> signing_keys) {
    this.signing_keys = signing_keys;
  }
}
