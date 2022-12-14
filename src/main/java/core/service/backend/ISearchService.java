package core.service.backend;

import api.dto.ModulePaginationDto;
import core.terraform.Module;
import extensions.security.core.SastReport;

import java.io.IOException;

public interface ISearchService {
  void bootstrap() throws Exception;
  ModulePaginationDto getModulesByRange(Integer offset, Integer limit) throws Exception;
  ModulePaginationDto getModulesByRangeAndTerm(String term, Integer offset, Integer limit) throws IOException;
  Module getModuleByName(String name) throws Exception;
  Module getModuleVersions(Module module) throws Exception;
  void ingestModuleData(Module module) throws Exception;
  void ingestSecurityScanResult(SastReport sastReport) throws Exception;
  void increaseDownloadCounter(Module module) throws IOException;
  SastReport getReportByModuleVersion(Module module) throws IOException;
}
