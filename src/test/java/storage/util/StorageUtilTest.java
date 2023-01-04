package storage.util;

import static org.junit.jupiter.api.Assertions.*;

import core.terraform.Module;
import org.junit.jupiter.api.Test;

class StorageUtilTest {
  final Module fakeModule = new Module("foo", "bar", "baz", "0.0.0");

  @Test
  void generateModuleStoragePath() {
    String storagePath = StorageUtil.generateModuleStoragePath(fakeModule);
    assertEquals(storagePath, "foo/bar/baz/0.0.0.zip");
  }
}