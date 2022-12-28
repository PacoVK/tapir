package core.event.consumer;

import core.service.backend.SearchService;
import core.terraform.Module;
import io.quarkus.vertx.ConsumeEvent;
import java.io.IOException;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;

@ApplicationScoped
public class DownloadListener {

  static final Logger LOGGER = Logger.getLogger(DownloadListener.class.getName());

  SearchService searchService;

  public DownloadListener(Instance<SearchService> searchServiceInstance) {
    this.searchService = searchServiceInstance.get();
  }

  @ConsumeEvent("module.download.requested")
  public String handleDownloadRequestedEvent(Module module) throws IOException {
    LOGGER.info(String.format("Download was requested for module %s, version %s",
            module.getName(),
            module.getCurrentVersion())
    );
    searchService.increaseDownloadCounter(module);
    LOGGER.fine(String.format("Download counter increased for module %s, version %s",
            module.getName(),
            module.getCurrentVersion())
    );
    return "ok";
  }
}
