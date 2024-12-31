package core.backend.azure.cosmosdb;

import api.dto.PaginationDto;
import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.implementation.NotFoundException;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import core.backend.TapirRepository;
import core.exceptions.DeployKeyNotFoundException;
import core.exceptions.ModuleNotFoundException;
import core.exceptions.ProviderNotFoundException;
import core.exceptions.ReportNotFoundException;
import core.tapir.CoreEntity;
import core.tapir.DeployKey;
import core.terraform.Module;
import core.terraform.Provider;
import extensions.core.Report;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@LookupIfProperty(name = "registry.search.backend", stringValue = "cosmosdb")
@ApplicationScoped
public class CosmosDbRepository extends TapirRepository {

  CosmosClient client;
  CosmosDatabase database;
  CosmosContainer modulesContainer;
  CosmosContainer providerContainer;
  CosmosContainer reportsContainer;
  CosmosContainer deployKeysContainer;
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
    this.deployKeysContainer = database.getContainer("DeployKeys");
  }

  @Override
  public void bootstrap() throws Exception {
    client.createDatabaseIfNotExists("tapir");
    createContainerIfNotExists("Modules");
    createContainerIfNotExists("Providers");
    createContainerIfNotExists("Reports");
    createContainerIfNotExists("DeployKeys");
  }

  private void createContainerIfNotExists(String name) {
    CosmosContainerProperties containerProperties = new CosmosContainerProperties(name, "/id");
    database.createContainerIfNotExists(containerProperties);
  }

  @Override
  public PaginationDto findModules(String identifier, Integer limit, String term) {
    List<SqlParameter> paramList = List.of(
            new SqlParameter("@namespace", "%" + term + "%"),
            new SqlParameter("@name", "%" + term + "%"),
            new SqlParameter("@provider", "%" + term + "%")
    );
    SqlQuerySpec querySpec = new SqlQuerySpec(
            "SELECT * FROM Modules m WHERE m.namespace "
                    + "LIKE @namespace OR m.name LIKE @name OR m.provider LIKE @provider",
            paramList);
    String continuationToken = identifier.isEmpty() ? null : identifier;
    FeedResponse<Module> feedResponse = modulesContainer
            .queryItems(
                    querySpec,
                    new CosmosQueryRequestOptions(),
                    Module.class)
            .streamByPage(continuationToken, limit).findFirst().orElse(null);
    if (feedResponse == null) {
      return new PaginationDto(Collections.EMPTY_LIST);
    }
    return new PaginationDto(feedResponse.getResults(), feedResponse.getContinuationToken());
  }

  @Override
  public PaginationDto findProviders(String identifier, Integer limit, String term) {
    List<SqlParameter> paramList = List.of(
            new SqlParameter("@namespace", "%" + term + "%"),
            new SqlParameter("@type", "%" + term + "%")
    );
    SqlQuerySpec querySpec = new SqlQuerySpec(
            "SELECT * FROM Providers p WHERE p.namespace LIKE @namespace OR p.type LIKE @type",
            paramList);
    String continuationToken = identifier.isEmpty() ? null : identifier;
    FeedResponse<Provider> feedResponse = providerContainer
            .queryItems(
                    querySpec,
                    new CosmosQueryRequestOptions(),
                    Provider.class)
            .streamByPage(continuationToken, limit).findFirst().orElse(null);
    if (feedResponse == null) {
      return new PaginationDto(Collections.EMPTY_LIST);
    }
    return new PaginationDto(feedResponse.getResults(), feedResponse.getContinuationToken());
  }

  @Override
  public PaginationDto findDeployKeys(String identifier, Integer limit, String term) throws Exception {
    List<SqlParameter> paramList = List.of(
        new SqlParameter("@id", "%" + term + "%"),
        new SqlParameter("@key", "%" + term + "%")
    );
    SqlQuerySpec querySpec = new SqlQuerySpec(
        "SELECT * FROM DeployKeys p WHERE p.id LIKE @id OR p.key LIKE @key",
        paramList);
    String continuationToken = identifier.isEmpty() ? null : identifier;
    FeedResponse<DeployKey> feedResponse = deployKeysContainer
        .queryItems(
            querySpec,
            new CosmosQueryRequestOptions(),
            DeployKey.class)
        .streamByPage(continuationToken, limit).findFirst().orElse(null);
    if (feedResponse == null) {
      return new PaginationDto(Collections.EMPTY_LIST);
    }
    return new PaginationDto(feedResponse.getResults(), feedResponse.getContinuationToken());
  }

  @Override
  public Module getModule(String id) throws ModuleNotFoundException {
    try {
      return modulesContainer.readItem(id, new PartitionKey(id), Module.class).getItem();
    } catch (NotFoundException cosmosException) {
      throw new ModuleNotFoundException(id, cosmosException);
    }
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
    } catch (NotFoundException cosmosException) {
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
    } catch (NotFoundException cosmosException) {
      providerToIngest = provider;
    }
    providerContainer.upsertItem(
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
  public Module increaseDownloadCounter(Module module) throws ModuleNotFoundException {
    Module existingModule = getModule(module.getId());
    Integer downloads = existingModule.getDownloads();
    existingModule.setDownloads(downloads + 1);
    modulesContainer.upsertItem(existingModule);
    return existingModule;
  }

  @Override
  public Report getReportByModuleVersion(Module module) throws ReportNotFoundException {
    String reportId = module.getId()
            + "-"
            + module.getCurrentVersion();
    try {
      return reportsContainer.readItem(reportId, new PartitionKey(reportId), Report.class)
              .getItem();
    } catch (NotFoundException cosmosException) {
      throw new ReportNotFoundException(reportId, cosmosException);
    }
  }

  @Override
  public Provider getProvider(String id) throws ProviderNotFoundException {
    try {
      return providerContainer.readItem(id, new PartitionKey(id), Provider.class).getItem();
    } catch (NotFoundException cosmosException) {
      throw new ProviderNotFoundException(id, cosmosException);
    }
  }

  @Override
  public DeployKey getDeployKeyById(String id) throws DeployKeyNotFoundException {
    try {
      return deployKeysContainer.readItem(id, new PartitionKey(id), DeployKey.class).getItem();
    } catch (NotFoundException cosmosException) {
      throw new DeployKeyNotFoundException(id, cosmosException);
    }
  }

  @Override
  public DeployKey getDeployKeyByValue(String value) throws DeployKeyNotFoundException {
      try {
          Collection<DeployKey> deployKeys = (Collection<DeployKey>) findDeployKeys("", 1, value).getEntities();
          if (deployKeys == null || deployKeys.isEmpty() {
            throw new DeployKeyNotFoundException("Could not find matching key");
          }

          return deployKeys
            .stream()
            .filter(deployKey -> deployKey.getKey().equals(value)) // Ensure exact match
            .findFirst()
            .orElseThrow(() -> new DeployKeyNotFoundException("Could not find matching key"));
      } catch (Exception e) {
          throw new DeployKeyNotFoundException("Could not find matching key");
      }
  }

  @Override
  public void saveDeployKey(DeployKey deployKey) throws Exception {
    deployKeysContainer.createItem(
            deployKey,
            new PartitionKey(deployKey.getId()),
            new CosmosItemRequestOptions()
    );
  }

  @Override
  public void updateDeployKey(DeployKey deployKey) throws Exception {
    deployKeysContainer.upsertItem(
        deployKey,
        new PartitionKey(deployKey.getId()),
        new CosmosItemRequestOptions()
    );
  }

  @Override
  public void deleteDeployKey(String id) throws Exception {
    deployKeysContainer.deleteItem(
            id,
            new PartitionKey(id),
            new CosmosItemRequestOptions()
    );
  }
}
