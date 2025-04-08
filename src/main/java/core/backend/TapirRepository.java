package core.backend;

import org.eclipse.microprofile.config.inject.ConfigProperty;

public abstract class TapirRepository implements IRepository {

  @ConfigProperty(name = "registry.search.table.names.modules")
  String moduleTableName;
  @ConfigProperty(name = "registry.search.table.names.provider")
  String providerTableName;
  @ConfigProperty(name = "registry.search.table.names.reports")
  String reportsTableName;
  @ConfigProperty(name = "registry.search.table.names.deployKeys")
  String deployKeyTableName;

  public String getModuleTableName() {
    return moduleTableName;
  }

  public String getProviderTableName() {
    return providerTableName;
  }

  public String getReportsTableName() {
    return reportsTableName;
  }

  public String getDeployKeyTableName() {
    return deployKeyTableName;
  }
}
