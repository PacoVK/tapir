package storage.util;

import core.terraform.Module;

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
}
