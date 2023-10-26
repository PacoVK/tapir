package core.vertx.event.consumer;

import core.service.ModuleService;
import core.terraform.Module;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.logging.Logger;

@ApplicationScoped
public class DownloadListener {

  static final Logger LOGGER = Logger.getLogger(DownloadListener.class.getName());

  ModuleService moduleService;

  public DownloadListener(ModuleService moduleService) {
    this.moduleService = moduleService;
  }

  @ConsumeEvent("module.download.requested")
  public Module handleDownloadRequestedEvent(Module module) throws Exception {
    LOGGER.info(String.format("Download was requested for module %s, version %s",
            module.getName(),
            module.getCurrentVersion())
    );
    Module updatedModule = moduleService.increaseDownloadCounter(module);
    LOGGER.fine(String.format("Download counter increased for module %s, version %s",
            module.getName(),
            module.getCurrentVersion())
    );
    return updatedModule;
  }
}
