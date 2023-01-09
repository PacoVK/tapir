package core.storage;

import org.eclipse.microprofile.config.ConfigProvider;

public abstract class StorageService implements IStorageInterface {

  protected Integer getAccessSessionDuration() {
    return ConfigProvider.getConfig().getValue(
            "registry.storage.access.session-duration", Integer.class);
  }
}
