package core.storage;

import core.exceptions.StorageException;
import core.terraform.Module;
import core.upload.FormData;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class AbstractStorageTest {

  public static final String UPLOADED_MODULE_FILENAME = "foo/bar/baz/1.0.0.zip";

  StorageService storageService;

  public AbstractStorageTest(StorageService storageService) {
    this.storageService = storageService;
  }

  protected void uploadModule() throws URISyntaxException, StorageException {
    Module fakeModule = new Module("foo", "bar", "baz", "1.0.0");
    URL resource = getClass().getClassLoader().getResource("test-archive.zip");
    assert resource != null;
    File archive=  new File(resource.toURI());
    FormData data = new FormData();
    data.setPayload(archive);
    data.setEntity(fakeModule);
    storageService.uploadModule(data);
  }
}
