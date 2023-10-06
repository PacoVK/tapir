package core.upload.service;

import core.service.ModuleService;
import core.service.ProviderService;
import core.service.StorageService;
import core.terraform.ArtifactVersion;
import core.terraform.Module;
import core.terraform.Provider;
import core.terraform.ProviderPlatform;
import core.upload.FormData;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;

@ApplicationScoped
public class UploadService {

  EventBus eventBus;
  StorageService storageService;
  ModuleService moduleService;
  ProviderService providerService;
  FileService fileService;

  public UploadService(
      StorageService storageService,
          ModuleService moduleService,
          ProviderService providerService,
          FileService fileService,
          EventBus eventBus) {
    this.storageService = storageService;
    this.moduleService = moduleService;
    this.providerService = providerService;
    this.fileService = fileService;
    this.eventBus = eventBus;
  }

  public void uploadModule(FormData archive) throws Exception {
    Module module = archive.getEntity();
    storageService.uploadModule(archive);
    moduleService.ingestModuleData(module);
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
    providerService.ingestProviderData(provider);
    eventBus.requestAndForget("provider.upload.finished", archive);
  }
}
