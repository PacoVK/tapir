package core.storage;

import core.exceptions.StorageException;
import core.upload.FormData;

public interface IStorageInterface {

  void uploadModule(FormData archive) throws StorageException;

  String getDownloadUrlForArtifact(String path) throws StorageException;

  void uploadProvider(FormData archive, String version) throws StorageException;
}
