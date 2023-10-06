package core.service;

import core.exceptions.StorageException;
import core.storage.StorageRepository;
import core.upload.FormData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;

@ApplicationScoped
public class StorageService {

  StorageRepository storageRepository;

  public StorageService(Instance<StorageRepository> storageServiceInstance) {
    this.storageRepository = storageServiceInstance.get();
  }

  public void uploadModule(FormData archive) throws StorageException {
    storageRepository.uploadModule(archive);
  }

  public void uploadProvider(FormData archive, String version) throws StorageException {
    storageRepository.uploadProvider(archive, version);
  }

  public String getDownloadUrlForArtifact(String path) throws StorageException {
    return storageRepository.getDownloadUrlForArtifact(path);
  }
}
