package core.vertx.event.consumer;

import core.upload.FormData;
import core.upload.service.FileService;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import io.vertx.mutiny.core.eventbus.EventBus;
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
  public String unpackArchive(FormData archive) {
    return "ok";
  }
}
