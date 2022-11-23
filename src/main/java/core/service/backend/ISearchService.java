package core.service.backend;

import core.service.upload.FormData;
import core.terraform.Module;

import java.util.Collection;

public interface ISearchService {
  void bootstrap() throws Exception;
  Collection<Module> getAllModules() throws Exception;
  Module getModuleByName(String name) throws Exception;
  Module getModuleVersions(Module module) throws Exception;
  void ingestModuleMetaData(Module module) throws Exception;
  void updateSecurityScanResult(FormData archive) throws Exception;
}
