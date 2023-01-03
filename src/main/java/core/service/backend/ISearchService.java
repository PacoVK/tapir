package core.service.backend;

import api.dto.ModulePagination;
import core.terraform.Module;
import extensions.core.SastReport;
import java.io.IOException;

public interface ISearchService {
  void bootstrap() throws Exception;

  ModulePagination findModules(String identifier, Integer limit, String term) throws Exception;

  Module getModuleById(String id) throws Exception;

  Module getModuleVersions(Module module) throws Exception;

  void ingestModuleData(Module module) throws Exception;

  void ingestSecurityScanResult(SastReport sastReport) throws Exception;

  Module increaseDownloadCounter(Module module) throws IOException;

  SastReport getReportByModuleVersion(Module module) throws IOException;
}
