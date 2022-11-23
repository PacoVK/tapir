package core.service.storage;

import core.terraform.Module;

public abstract class StorageService implements IStorageInterface{

  public String getDownloadUrlForModule(Module module){
    return module.getName();
  }
}
