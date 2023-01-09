package core.backend;

import api.dto.ModulePagination;
import core.exceptions.ReportNotFoundException;
import core.terraform.Module;
import extensions.core.Report;
import java.io.IOException;

public interface ISearchService {
  void bootstrap() throws Exception;

  ModulePagination findModules(String identifier, Integer limit, String term) throws Exception;

  Module getModuleById(String id) throws Exception;

  Module getModuleVersions(Module module) throws Exception;

  void ingestModuleData(Module module) throws Exception;

  void ingestSecurityScanResult(Report report) throws Exception;

  Module increaseDownloadCounter(Module module) throws IOException;

  Report getReportByModuleVersion(Module module) throws IOException, ReportNotFoundException;
}
