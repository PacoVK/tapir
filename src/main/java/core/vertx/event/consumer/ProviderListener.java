package core.vertx.event.consumer;

import core.terraform.Provider;
import core.upload.FormData;
import core.upload.service.FileService;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import io.vertx.mutiny.core.eventbus.EventBus;
import java.io.IOException;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProviderListener {

  static final Logger LOGGER = Logger.getLogger(ProviderListener.class.getName());

  FileService fileService;
  EventBus eventBus;

  public ProviderListener(FileService fileService, EventBus eventBus) {
    this.fileService = fileService;
    this.eventBus = eventBus;
  }

  @Blocking
  @ConsumeEvent("provider.upload.finished")
  public String unpackArchive(FormData archive) throws IOException {
    Provider provider = archive.getEntity();
    LOGGER.info(String.format("Clean up temporary provider files for provider %s, version %s",
            provider.getId(),
            provider.getCurrentVersion())
    );
    fileService.deleteAllFilesInDirectory(archive.getCompressedFile().getParentFile());
    LOGGER.info(String.format("Clean up successful for provider %s, version %s",
            provider.getId(),
            provider.getCurrentVersion())
    );
    return "ok";
  }
}
