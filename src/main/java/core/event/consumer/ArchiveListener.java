package core.event.consumer;

import core.service.upload.FileService;
import core.service.upload.FormData;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import io.vertx.mutiny.core.eventbus.EventBus;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ArchiveListener {

  static final Logger LOGGER = Logger.getLogger(ArchiveListener.class.getName());

  public ArchiveListener(FileService fileService, EventBus eventBus) {
    this.fileService = fileService;
    this.eventBus = eventBus;
  }

  FileService fileService;
  EventBus eventBus;

  @Blocking
  @ConsumeEvent("module.upload.finished")
  public String unpackArchive(FormData archive) {
    LOGGER.info(String.format("Start to unpack module %s, version %s",
            archive.getModule().getName(),
            archive.getModule().getCurrentVersion())
    );
    File tmpArchiveFile = archive.getCompressedModule();
    Path tmpDirectory = tmpArchiveFile.toPath().getParent();
    fileService.unpackArchive(tmpArchiveFile, tmpDirectory);
    eventBus.requestAndForget("module.extract.finished", archive);
    return "ok";
  }

  @ConsumeEvent("module.processing.finished")
  public String cleanUp(FormData  archive) throws IOException {
    LOGGER.info(String.format("Clean up temporary module files for module %s, version %s",
            archive.getModule().getName(),
            archive.getModule().getCurrentVersion())
    );
    fileService.deleteAllFilesInDirectory(archive.getCompressedModule().getParentFile());
    LOGGER.info(String.format("Clean up successful for module %s, version %s",
            archive.getModule().getName(),
            archive.getModule().getCurrentVersion())
    );
    return "ok";
  }
}
