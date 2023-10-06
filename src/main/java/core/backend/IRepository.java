package core.backend;

import api.dto.PaginationDto;
import core.exceptions.DeployKeyNotFoundException;
import core.exceptions.ReportNotFoundException;
import core.tapir.DeployKey;
import core.terraform.Module;
import core.terraform.Provider;
import extensions.core.Report;
import java.io.IOException;

public interface IRepository {
  void bootstrap() throws Exception;

  PaginationDto findModules(String identifier, Integer limit, String term) throws Exception;

  PaginationDto findProviders(String identifier, Integer limit, String term) throws Exception;

  PaginationDto findDeployKeys(String identifier, Integer limit, String term) throws Exception;

  Module getModule(String id) throws Exception;

  void ingestModuleData(Module module) throws Exception;

  void ingestProviderData(Provider provider) throws Exception;

  void ingestSecurityScanResult(Report report) throws Exception;

  Module increaseDownloadCounter(Module module) throws Exception;

  Report getReportByModuleVersion(Module module) throws IOException, ReportNotFoundException;

  Provider getProvider(String id) throws Exception;

  DeployKey getDeployKeyById(String id) throws DeployKeyNotFoundException;

  void saveDeployKey(DeployKey deployKey) throws Exception;

  void updateDeployKey(DeployKey deployKey) throws Exception;

  void deleteDeployKey(String id) throws Exception;
}
