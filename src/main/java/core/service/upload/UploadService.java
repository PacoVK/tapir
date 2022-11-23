package core.service.upload;

import core.service.backend.SearchService;
import core.service.storage.StorageService;
import core.terraform.Module;
import io.vertx.mutiny.core.eventbus.EventBus;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import java.io.File;

@ApplicationScoped
public class UploadService {

  EventBus eventBus;
  StorageService storageService;
  SearchService searchService;
  FileService fileService;

  public UploadService(Instance<StorageService> storageServiceInstance, Instance<SearchService> searchServiceInstance, FileService fileService, EventBus eventBus) {
    this.storageService = storageServiceInstance.get();
    this.searchService = searchServiceInstance.get();
    this.fileService = fileService;
    this.eventBus = eventBus;
  }

  public void uploadModule(FormData archive) throws Exception {
    Module module = archive.getModule();
    storageService.uploadModule(archive);
    searchService.ingestModuleMetaData(module);
    File tmpArchiveFile = fileService.createTempModuleArchiveFile(archive.getModule());
    fileService.flushArchiveToFile(archive.getPayload(), tmpArchiveFile);
    archive.setCompressedModule(tmpArchiveFile);
    eventBus.requestAndForget("module.upload", archive);
  }
}
