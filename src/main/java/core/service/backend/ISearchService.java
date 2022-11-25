package core.service.backend;

import core.terraform.Module;
import extensions.security.core.SastReport;

import java.io.IOException;
import java.util.Collection;

public interface ISearchService {
  void bootstrap() throws Exception;
  Collection<Module> getAllModules() throws Exception;
  Module getModuleByName(String name) throws Exception;
  Module getModuleVersions(Module module) throws Exception;
  void ingestModuleData(Module module) throws Exception;
  void ingestSecurityScanResult(SastReport sastReport) throws Exception;
  void increaseDownloadCounter(Module module) throws IOException;
}
