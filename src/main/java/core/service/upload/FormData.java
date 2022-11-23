package core.service.upload;

import core.terraform.Module;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import javax.ws.rs.core.MediaType;
import java.io.File;

public class FormData {

  @RestForm("module_archive")
  private File payload;

  private File compressedModule;

  @RestForm
  @PartType(MediaType.TEXT_PLAIN)
  private String mimeType = "application/zip";
  private Module module;

  public Module getModule() {
    return module;
  }

  public void setModule(Module module) {
    this.module = module;
  }

  public File getCompressedModule() {
    return compressedModule;
  }

  public void setCompressedModule(File compressedModule) {
    this.compressedModule = compressedModule;
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
