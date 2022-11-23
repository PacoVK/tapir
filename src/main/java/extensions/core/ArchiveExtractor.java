package extensions.core;

import core.service.upload.FileService;
import core.service.upload.FormData;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.nio.file.Path;
import java.util.logging.Logger;

@ApplicationScoped
public class ArchiveExtractor {

  static final Logger LOGGER = Logger.getLogger(ArchiveExtractor.class.getName());

  public ArchiveExtractor(FileService fileService, EventBus eventBus) {
    this.fileService = fileService;
    this.eventBus = eventBus;
  }

  FileService fileService;
  EventBus eventBus;

  @ConsumeEvent("module.upload")
  public void unpackArchive(FormData archive) {
    LOGGER.info(String.format("Start to unpack module %s, version %s", archive.getModule().getName(), archive.getModule().getCurrentVersion()));
    File tmpArchiveFile = archive.getCompressedModule();
    Path tmpDirectory = tmpArchiveFile.toPath().getParent();
    fileService.unpackArchive(tmpArchiveFile, tmpDirectory);
    eventBus.requestAndForget("module.scan", archive);
  }

  @ConsumeEvent("publish.result")
  public void cleanUp(FormData archive) throws IOException {
    LOGGER.info(String.format("Clean up temporary module files for module %s, version %s", archive.getModule().getName(), archive.getModule().getCurrentVersion()));
    fileService.deleteAllFilesInDirectory(archive.getCompressedModule().getParentFile());
    LOGGER.info(String.format("Clean up successful for module %s, version %s", archive.getModule().getName(), archive.getModule().getCurrentVersion()));
  }
}
