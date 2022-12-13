package api.dto;

import core.terraform.Module;

import java.util.Collection;

public class ModulePaginationDto {

  public ModulePaginationDto(Collection<Module> modules, Integer totalModulesCount) {
    this.modules = modules;
    this.totalModulesCount = totalModulesCount;
  }

  Collection<Module> modules;
  Integer totalModulesCount;

  public Collection<Module> getModules() {
    return modules;
  }

  public void setModules(Collection<Module> modules) {
    this.modules = modules;
  }

  public Integer getTotalModulesCount() {
    return totalModulesCount;
  }

  public void setTotalModulesCount(Integer totalModulesCount) {
    this.totalModulesCount = totalModulesCount;
  }
}
