package core.backend.azure.cosmosdb;

import api.dto.PaginationDto;
import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import core.backend.SearchService;
import core.exceptions.ReportNotFoundException;
import core.terraform.Module;
import core.terraform.Provider;
import extensions.core.Report;
import io.quarkus.arc.lookup.LookupIfProperty;
import java.io.IOException;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@LookupIfProperty(name = "registry.search.backend", stringValue = "cosmosdb")
@ApplicationScoped
public class CosmosDbRepository extends SearchService {

  static final Logger LOGGER = Logger.getLogger(CosmosDbRepository.class.getName());

  CosmosClient client;
  CosmosDatabase database;
  CosmosContainer modulesContainer;
  CosmosContainer providerContainer;
  CosmosContainer reportsContainer;
  @ConfigProperty(name = "registry.search.azure.endpoint")
  String endpoint;
  @ConfigProperty(name = "registry.search.azure.master-key")
  String masterKey;

  @PostConstruct
  public void initialize() {
    this.client = new CosmosClientBuilder()
            .endpoint(endpoint)
            .key(masterKey)
            .consistencyLevel(ConsistencyLevel.EVENTUAL)
            .contentResponseOnWriteEnabled(true)
            .buildClient();
    this.database = client.getDatabase("tapir");
    this.modulesContainer = database.getContainer("Modules");
    this.providerContainer = database.getContainer("Providers");
    this.reportsContainer = database.getContainer("Reports");
  }

  @Override
  public void bootstrap() throws Exception {
    client.createDatabaseIfNotExists("tapir");

    CosmosContainerProperties modulesContainerProperties = new CosmosContainerProperties("Modules", "/id");
    database.createContainerIfNotExists(modulesContainerProperties);

    CosmosContainerProperties providerContainerProperties = new CosmosContainerProperties("Providers", "/id");
    database.createContainerIfNotExists(providerContainerProperties);

    CosmosContainerProperties reportContainerProperties = new CosmosContainerProperties("Reports", "/id");
    database.createContainerIfNotExists(reportContainerProperties);

  }

  @Override
  public PaginationDto findModules(String identifier, Integer limit, String term) throws Exception {
    return null;
  }

  @Override
  public PaginationDto findProviders(String identifier, Integer limit, String term) throws Exception {
    return null;
  }

  @Override
  public Module getModuleById(String id) throws Exception {
    return modulesContainer.readItem(id, new PartitionKey(id), Module.class).getItem();
  }

  @Override
  public Module getModuleVersions(Module module) {
    return null;
  }

  @Override
  public void ingestModuleData(Module module) {
    Module moduleToIngest;
    try {
      Module existingModule = modulesContainer.readItem(
              module.getId(),
              new PartitionKey(module.getId()),
              Module.class
      ).getItem();
      existingModule.getVersions().add(module.getVersions().first());
      existingModule.setPublished_at(module.getPublished_at());
      moduleToIngest = existingModule;
    } catch (CosmosException cosmosException) {
      moduleToIngest = module;
    }
    modulesContainer.upsertItem(
            moduleToIngest,
            new PartitionKey(module.getId()),
            new CosmosItemRequestOptions()
    );
  }

  @Override
  public void ingestProviderData(Provider provider) {
    Provider providerToIngest;
    try {
      Provider existingProvider = providerContainer.readItem(
              provider.getId(),
              new PartitionKey(provider.getId()),
              Provider.class
      ).getItem();
      existingProvider
          .getVersions()
          .put(
                  provider.getVersions().firstEntry().getKey(),
                  provider.getVersions().firstEntry().getValue()
          );
      providerToIngest = existingProvider;
    } catch (CosmosException cosmosException) {
      providerToIngest = provider;
    }
    providerContainer.createItem(
            providerToIngest,
            new PartitionKey(provider.getId()),
            new CosmosItemRequestOptions()
    );
  }

  @Override
  public void ingestSecurityScanResult(Report report) {
    reportsContainer.upsertItem(
            report,
            new PartitionKey(report.getId()),
            new CosmosItemRequestOptions()
    );
  }

  @Override
  public Module increaseDownloadCounter(Module module) {
    return null;
  }

  @Override
  public Report getReportByModuleVersion(Module module) throws IOException, ReportNotFoundException {
    return null;
  }

  @Override
  public Provider getProviderById(String id) {
    return providerContainer.readItem(id, new PartitionKey(id), Provider.class).getItem();
  }
}
