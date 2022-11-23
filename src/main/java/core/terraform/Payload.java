package core.terraform;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collection;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payload {
  public Meta meta;
  public Collection<Module> modules;

  public Meta getMeta() {
    return meta;
  }

  public void setMeta(Meta meta) {
    this.meta = meta;
  }

  public Collection<Module> getModules() {
    return modules;
  }

  public void setModules(Collection<Module> modules) {
    this.modules = modules;
  }
}
