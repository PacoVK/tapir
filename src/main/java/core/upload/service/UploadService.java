package core.upload.service;

import core.backend.SearchService;
import core.storage.StorageService;
import core.terraform.ArtifactVersion;
import core.terraform.Module;
import core.terraform.Provider;
import core.terraform.ProviderPlatform;
import core.upload.FormData;
import io.vertx.mutiny.core.eventbus.EventBus;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;

@ApplicationScoped
public class UploadService {

  EventBus eventBus;
  StorageService storageService;
  SearchService searchService;
  FileService fileService;

  public UploadService(
          Instance<StorageService> storageServiceInstance,
          Instance<SearchService> searchServiceInstance,
          FileService fileService,
          EventBus eventBus) {
    this.storageService = storageServiceInstance.get();
    this.searchService = searchServiceInstance.get();
    this.fileService = fileService;
    this.eventBus = eventBus;
  }

  public void uploadModule(FormData archive) throws Exception {
    Module module = archive.getEntity();
    storageService.uploadModule(archive);
    searchService.ingestModuleData(module);
    File tmpArchiveFile = fileService.createTempArchiveFile(
            String.format("%s-%s", module.getName(), module.getCurrentVersion())
    );
    fileService.flushArchiveToFile(archive.getPayload(), tmpArchiveFile);
    archive.setCompressedFile(tmpArchiveFile);
    eventBus.requestAndForget("module.upload.finished", archive);
  }

  public void uploadProvider(FormData archive, String version) throws Exception {
    Provider provider = archive.getEntity();
    File tmpArchiveFile = fileService.createTempArchiveFile(
            String.format("%s-%s", provider.getId(), version)
    );
    fileService.flushArchiveToFile(archive.getPayload(), tmpArchiveFile);
    archive.setCompressedFile(tmpArchiveFile);
    Path tmpDirectory = tmpArchiveFile.toPath().getParent();
    fileService.unpackArchive(tmpArchiveFile, tmpDirectory);
    List<ProviderPlatform> platforms = fileService
            .getProviderPlatformsFromShaSumsFile(tmpDirectory.toFile());
    TreeMap<ArtifactVersion, List<ProviderPlatform>> versionListTreeMap = new TreeMap<>();
    versionListTreeMap.put(new ArtifactVersion(version), platforms);
    provider.setVersions(versionListTreeMap);
    storageService.uploadProvider(archive, version);
    searchService.ingestProviderData(provider);
    eventBus.requestAndForget("provider.upload.finished", archive);
  }
}
