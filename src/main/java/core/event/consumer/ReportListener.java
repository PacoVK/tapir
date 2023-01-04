package core.event.consumer;

import core.service.backend.SearchService;
import extensions.core.SastReport;
import io.quarkus.vertx.ConsumeEvent;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;

@ApplicationScoped
public class ReportListener {

  static final Logger LOGGER = Logger.getLogger(ReportListener.class.getName());

  SearchService searchService;

  public ReportListener(Instance<SearchService> searchServiceInstance) {
    this.searchService = searchServiceInstance.get();
  }

  @ConsumeEvent("module.report.finished")
  public String handleSecurityReportUpdate(SastReport sastReport) throws Exception {
    LOGGER.info(String.format("Publishing scan result for module %s, version %s",
            sastReport.getModuleName(),
            sastReport.getModuleVersion())
    );
    searchService.ingestSecurityScanResult(sastReport);
    return "ok";
  }
}
