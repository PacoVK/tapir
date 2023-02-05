package core.upload;

import core.terraform.CoreEntity;
import java.io.File;
import javax.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

public class FormData {

  @RestForm("archive")
  private File payload;

  private File compressedFile;

  @RestForm
  @PartType(MediaType.TEXT_PLAIN)
  private String mimeType = "application/zip";
  private CoreEntity entity;

  public <T extends CoreEntity> T getEntity() {
    return (T) entity;
  }

  public <T extends CoreEntity> void setEntity(T entity) {
    this.entity = entity;
  }

  public File getCompressedFile() {
    return compressedFile;
  }

  public void setCompressedFile(File compressedFile) {
    this.compressedFile = compressedFile;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public File getPayload() {
    return payload;
  }
}
