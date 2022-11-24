package core.event.consumer;

import core.service.backend.SearchService;
import extensions.security.core.SastReport;
import io.quarkus.vertx.ConsumeEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import java.util.logging.Logger;

@ApplicationScoped
public class ReportListener {

  static final Logger LOGGER = Logger.getLogger(ReportListener.class.getName());

  SearchService searchService;

  public ReportListener(Instance<SearchService> searchServiceInstance) {
    this.searchService = searchServiceInstance.get();
  }

  @ConsumeEvent("module.report.finished")
  public void handleSecurityReportUpdate(SastReport sastReport) throws Exception {
    LOGGER.info(String.format("Publishing scan result for module %s, version %s", sastReport.getModuleName(), sastReport.getModuleVersion()));
    searchService.ingestSecurityScanResult(sastReport);
  }
}
