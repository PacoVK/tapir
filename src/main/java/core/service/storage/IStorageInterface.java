package core.service.storage;

import core.service.upload.FormData;
import core.terraform.Module;

public interface IStorageInterface {

  void uploadModule(FormData archive);

  String getDownloadUrlForModule(Module module);
}
