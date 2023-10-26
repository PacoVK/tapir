package core.storage;

import core.exceptions.StorageException;
import core.upload.FormData;
import org.eclipse.microprofile.config.ConfigProvider;

public abstract class StorageRepository {

  public abstract void uploadModule(FormData archive) throws StorageException;

  public abstract String getDownloadUrlForArtifact(String path) throws StorageException;

  public abstract void uploadProvider(FormData archive, String version) throws StorageException;

  protected Integer getAccessSessionDuration() {
    return ConfigProvider.getConfig().getValue(
            "registry.storage.access.session-duration", Integer.class);
  }
}
