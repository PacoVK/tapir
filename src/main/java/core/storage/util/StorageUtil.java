package core.storage.util;

import core.terraform.Module;
import core.terraform.Provider;

public class StorageUtil {
  public static String generateModuleStoragePath(Module module) {
    return new StringBuilder(
            String.format("%s/%s/%s",
                    module.getNamespace(),
                    module.getName(),
                    module.getProvider()
            )
    )
            .append("/")
            .append(module.getCurrentVersion())
            .append(".zip")
            .toString();
  }

  public static String generateProviderStorageDirectory(Provider provider, String version) {
    return String.format("%s/%s/%s",
            provider.getNamespace(),
            provider.getType(),
            version
    );
  }
}
