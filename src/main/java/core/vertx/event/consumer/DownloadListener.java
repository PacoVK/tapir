package core.vertx.event.consumer;

import core.backend.SearchService;
import core.terraform.Module;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import java.util.logging.Logger;

@ApplicationScoped
public class DownloadListener {

  static final Logger LOGGER = Logger.getLogger(DownloadListener.class.getName());

  SearchService searchService;

  public DownloadListener(Instance<SearchService> searchServiceInstance) {
    this.searchService = searchServiceInstance.get();
  }

  @ConsumeEvent("module.download.requested")
  public Module handleDownloadRequestedEvent(Module module) throws Exception {
    LOGGER.info(String.format("Download was requested for module %s, version %s",
            module.getName(),
            module.getCurrentVersion())
    );
    Module updatedModule = searchService.increaseDownloadCounter(module);
    LOGGER.fine(String.format("Download counter increased for module %s, version %s",
            module.getName(),
            module.getCurrentVersion())
    );
    return updatedModule;
  }
}
