package core.storage;

import core.exceptions.StorageException;
import core.terraform.Module;
import core.terraform.Provider;
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
    URL resource = getClass().getClassLoader().getResource("test-module.zip");
    assert resource != null;
    File archive =  new File(resource.toURI());
    FormData data = new FormData();
    data.setPayload(archive);
    data.setEntity(fakeModule);
    storageService.uploadModule(data);
  }

  protected void uploadProvider() throws URISyntaxException, StorageException {
    Provider fakeProvider = new Provider("foo", "bar");
    URL resource = getClass().getClassLoader().getResource("providerTest/unpackedProvider");
    assert resource != null;
    File archive =  new File(resource.toURI());
    FormData data = new FormData();
    data.setCompressedFile(archive);
    data.setEntity(fakeProvider);
    storageService.uploadProvider(data, "1.0.0");
  }

  protected String getDownloadUrlForArtifact() throws StorageException {
    return storageService.getDownloadUrlForArtifact("foo/bar");
  }
}
