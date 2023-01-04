package api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import core.terraform.Module;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Collection;
import java.util.List;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModulePagination {

  public ModulePagination(List<Module> modules) {
    this.modules = modules;
    if (!modules.isEmpty()) {
      this.lastEvaluatedItem = modules.get(modules.size() - 1);
    }
  }

  Collection<Module> modules;
  Module lastEvaluatedItem;

  public Collection<Module> getModules() {
    return modules;
  }

  public void setModules(Collection<Module> modules) {
    this.modules = modules;
  }

  public Module getLastEvaluatedItem() {
    return lastEvaluatedItem;
  }

  public void setLastEvaluatedItem(Module lastEvaluatedItem) {
    this.lastEvaluatedItem = lastEvaluatedItem;
  }
}
