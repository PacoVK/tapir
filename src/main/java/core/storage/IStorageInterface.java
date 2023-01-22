package core.storage;

import core.exceptions.StorageException;
import core.terraform.Module;
import core.upload.FormData;

public interface IStorageInterface {

  void uploadModule(FormData archive) throws StorageException;

  String getDownloadUrlForModule(Module module) throws StorageException;
}
