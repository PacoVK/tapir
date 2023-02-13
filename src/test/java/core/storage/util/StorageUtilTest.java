package core.storage.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import core.terraform.Module;
import core.terraform.Provider;
import org.junit.jupiter.api.Test;

class StorageUtilTest {

  @Test
  void generateModuleStoragePath() {
    Module fakeModule = new Module("foo", "bar", "baz", "0.0.0");
    String storagePath = StorageUtil.generateModuleStoragePath(fakeModule);
    assertEquals(storagePath, "foo/bar/baz/0.0.0.zip");
  }

  @Test
  void generateProviderStorageDirectory() {
    Provider fakeProvider = new Provider("foo", "bar");
    String storagePath = StorageUtil.generateProviderStorageDirectory(fakeProvider, "0.0.0");
    assertEquals(storagePath, "foo/bar/0.0.0");
  }
}