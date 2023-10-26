package core.service;

import api.dto.PaginationDto;
import core.backend.TapirRepository;
import core.terraform.ArtifactVersion;
import core.terraform.Module;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import java.util.TreeSet;

@ApplicationScoped
public class ModuleService {
  TapirRepository tapirRepository;

  public ModuleService(Instance<TapirRepository> tapirRepositoryInstance) {
    this.tapirRepository = tapirRepositoryInstance.get();
  }

  public Module getModule(String id) throws Exception {
    return tapirRepository.getModule(id);
  }

  public PaginationDto getModules(String identifier, Integer limit, String terms) throws Exception {
    return tapirRepository.findModules(identifier, limit, terms);
  }

  public TreeSet<ArtifactVersion> getModuleVersions(Module module) throws Exception {
    Module fetchedModule = getModule(module.getId());
    return fetchedModule.getVersions();
  }

  public Module increaseDownloadCounter(Module module) throws Exception {
    return tapirRepository.increaseDownloadCounter(module);
  }

  public void ingestModuleData(Module module) throws Exception {
    tapirRepository.ingestModuleData(module);
  }
}
