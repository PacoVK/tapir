package core.storage.util;

import core.terraform.Module;
import core.terraform.Provider;

public class StorageUtil {

  public static String generateModuleStoragePath(Module module) {
    return module.getNamespace()
            + "/"
            + module.getName()
            + "/"
            + module.getProvider()
            + "/"
            + module.getCurrentVersion()
            + ".zip";
  }

  public static String generateProviderStorageDirectory(Provider provider, String version) {
    return  provider.getNamespace()
            + "/"
            + provider.getType()
            + "/"
            + version;
  }
}
