package core.vertx.event.consumer;

import core.terraform.Module;
import core.upload.FormData;
import core.upload.service.FileService;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

@ApplicationScoped
public class ModuleListener {

  static final Logger LOGGER = Logger.getLogger(ModuleListener.class.getName());

  public ModuleListener(FileService fileService, EventBus eventBus) {
    this.fileService = fileService;
    this.eventBus = eventBus;
  }

  FileService fileService;
  EventBus eventBus;

  @Blocking
  @ConsumeEvent("module.upload.finished")
  public String unpackArchive(FormData archive) {
    Module module = archive.getEntity();
    LOGGER.info(String.format("Start to unpack module %s, version %s",
            module.getName(),
            module.getCurrentVersion())
    );
    File tmpArchiveFile = archive.getCompressedFile();
    Path tmpDirectory = tmpArchiveFile.toPath().getParent();
    fileService.unpackArchive(tmpArchiveFile, tmpDirectory);
    eventBus.publish("module.extract.finished", archive);
    return "ok";
  }

  @ConsumeEvent("module.processing.finished")
  public String cleanUp(FormData  archive) throws IOException {
    Module module = archive.getEntity();
    LOGGER.info(String.format("Clean up temporary module files for module %s, version %s",
            module.getName(),
            module.getCurrentVersion())
    );
    fileService.deleteAllFilesInDirectory(archive.getCompressedFile().getParentFile());
    LOGGER.info(String.format("Clean up successful for module %s, version %s",
            module.getName(),
            module.getCurrentVersion())
    );
    return "ok";
  }
}
