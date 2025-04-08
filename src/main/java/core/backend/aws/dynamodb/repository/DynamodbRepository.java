package core.backend.aws.dynamodb.repository;

import api.dto.PaginationDto;
import core.backend.TapirRepository;
import core.exceptions.DeployKeyNotFoundException;
import core.exceptions.ModuleNotFoundException;
import core.exceptions.ProviderNotFoundException;
import core.exceptions.ReportNotFoundException;
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
import java.util.Map;
import java.util.logging.Logger;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

@LookupIfProperty(name = "registry.search.backend", stringValue = "dynamodb")
@ApplicationScoped
public class DynamodbRepository extends TapirRepository {

  static final Logger LOGGER = Logger.getLogger(DynamodbRepository.class.getName());

  DynamoDbTable<Module> modulesTable;
  DynamoDbTable<Report> reportsTable;
  DynamoDbTable<Provider> providerTable;
  DynamoDbTable<DeployKey> deployKeysTable;
  final DynamoDbClient dynamoDbClient;
  final TableSchema<Provider> providerTableSchema = TableSchemas.providerTableSchema;
  final TableSchema<Module> moduleTableSchema = TableSchemas.moduleTableSchema;
  final TableSchema<Report> reportsTableSchema = TableSchemas.reportsTableSchema;
  final TableSchema<DeployKey> deployKeyTableSchema = TableSchemas.deployKeysTableSchema;

  public DynamodbRepository(DynamoDbClient dynamoDbClient) {
    this.dynamoDbClient = dynamoDbClient;
  }

  @PostConstruct
  void postConstruct() {
    DynamoDbEnhancedClient dbEnhancedClient = DynamoDbEnhancedClient
        .builder().dynamoDbClient(dynamoDbClient).build();
    this.modulesTable = dbEnhancedClient.table(getModuleTableName(), moduleTableSchema);
    this.providerTable = dbEnhancedClient.table(getProviderTableName(), providerTableSchema);
    this.reportsTable = dbEnhancedClient.table(getReportsTableName(), reportsTableSchema);
    this.deployKeysTable = dbEnhancedClient.table(getDeployKeyTableName(), deployKeyTableSchema);
  }

  @Override
  public Provider getProvider(String id) throws Exception {
    Provider provider = providerTable.getItem(Key.builder().partitionValue(id).build());
    if (provider == null) {
      throw new ProviderNotFoundException(id);
    }
    return provider;
  }

  @Override
  public void ingestModuleData(Module module) {
    Module moduleToIngest;
    Module existingModule = modulesTable.getItem(module);
    if (existingModule != null) {
      existingModule.getVersions().add(module.getVersions().first());
      existingModule.setPublished_at(module.getPublished_at());
      moduleToIngest = existingModule;
    } else {
      moduleToIngest = module;
    }
    modulesTable.updateItem(moduleToIngest);
  }

  @Override
  public void ingestProviderData(Provider provider) {
    Provider providerToIngest;
    Provider existingProvider = providerTable.getItem(provider);
    if (existingProvider != null) {
      existingProvider
          .getVersions()
          .put(
            provider.getVersions().firstEntry().getKey(),
            provider.getVersions().firstEntry().getValue()
          );
      providerToIngest = existingProvider;
    } else {
      providerToIngest = provider;
    }
    providerTable.updateItem(providerToIngest);
  }

  public PaginationDto findProviders(String identifier, Integer limit, String terms) {
    String whereClause = "contains(#namespace, :term) or contains(#type, :term)";
    Expression expression = Expression.builder()
            .expressionNames(Map.of(
                    "#namespace", "namespace",
                    "#type", "type")
            )
            .expressionValues(Map.of(":term", AttributeValue.fromS(terms)))
            .expression(whereClause)
            .build();
    ScanEnhancedRequest scanEnhancedRequest = buildScanRequest(
            identifier,
            limit,
            terms,
            expression
    );
    Page<Provider> providerPage = providerTable.scan(scanEnhancedRequest)
            .stream().findFirst().orElse(null);
    if (providerPage == null) {
      return new PaginationDto(Collections.EMPTY_LIST);
    }
    return new PaginationDto(providerPage.items());
  }

  @Override
  public void ingestSecurityScanResult(Report report) {
    reportsTable.putItem(report);
  }

  @Override
  public Module increaseDownloadCounter(Module module) {
    Module existingModule = modulesTable.getItem(module);
    if (existingModule != null) {
      Integer downloads = existingModule.getDownloads();
      existingModule.setDownloads(downloads + 1);
    }
    modulesTable.updateItem(existingModule);
    return existingModule;
  }

  @Override
  public Report getReportByModuleVersion(Module module) throws ReportNotFoundException {
    String reportId = module.getId()
            + "-"
            + module.getCurrentVersion();
    return getReportById(reportId);
  }

  public void bootstrap() {
    createTable(modulesTable);
    createTable(providerTable);
    createTable(reportsTable);
    createTable(deployKeysTable);
  }

  private void createTable(DynamoDbTable<?> table) {
    try {
      table.createTable();
    } catch (ResourceInUseException inUseException) {
      LOGGER.info(String.format("Table %s is already existing", table.tableName()));
      return;
    }
    try (DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dynamoDbClient).build()) {
      ResponseOrException<DescribeTableResponse> response = waiter
              .waitUntilTableExists(builder -> builder.tableName(getModuleTableName()).build())
              .matched();
      response.response().orElseThrow(
              () -> new RuntimeException(
                      String.format("Table %s was not created", table.tableName()
                      )));
      LOGGER.info(String.format("Table %s was created", table.tableName()));
    } catch (Exception e) {
      LOGGER.warning(
              String.format(
                      "Failed to verify that table %s was actually created",
                      table.tableName()
              )
      );
    }
  }

  private ScanEnhancedRequest buildScanRequest(
          String identifier,
          Integer limit,
          String terms,
          Expression filterExpression
  ) {
    ScanEnhancedRequest.Builder scanEnhancedRequestBuilder = ScanEnhancedRequest.builder()
            .limit(limit);
    if (!identifier.isEmpty()) {
      scanEnhancedRequestBuilder.exclusiveStartKey(Map.of("id", AttributeValue.fromS(identifier)));
    }
    if (!terms.isEmpty()) {
      scanEnhancedRequestBuilder.filterExpression(filterExpression);
    }
    return scanEnhancedRequestBuilder.build();
  }

  public PaginationDto findModules(String identifier, Integer limit, String terms) {
    String whereClause = "contains(#namespace, :term)"
            + "or contains(#name, :term)"
            + "or contains(#provider, :term)";
    Expression expression = Expression.builder()
            .expressionNames(Map.of(
                    "#namespace", "namespace",
                    "#name", "name",
                    "#provider", "provider")
            )
            .expressionValues(Map.of(":term", AttributeValue.fromS(terms)))
            .expression(whereClause)
            .build();
    ScanEnhancedRequest scanEnhancedRequest = buildScanRequest(
            identifier,
            limit,
            terms,
            expression
    );
    Page<Module> modulePage = modulesTable.scan(scanEnhancedRequest)
            .stream().findFirst().orElse(Page.create(List.of()));
    return new PaginationDto(modulePage.items());
  }

  public Module getModule(String name) throws ModuleNotFoundException {
    Module module = modulesTable.getItem(Key.builder().partitionValue(name).build());
    if (module == null) {
      throw new ModuleNotFoundException(name);
    }
    return module;
  }

  public Report getReportById(String name) throws ReportNotFoundException {
    Report report = reportsTable.getItem(Key.builder().partitionValue(name).build());
    if (report == null) {
      throw new ReportNotFoundException(name);
    }
    return report;
  }

  public void saveDeployKey(DeployKey deployKey) {
    deployKeysTable.putItem(r -> r.item(deployKey).conditionExpression(
        Expression.builder()
            .expression("attribute_not_exists(id)")
            .build()));
  }

  public void updateDeployKey(DeployKey deployKey) {
    deployKeysTable.putItem(deployKey);
  }

  public DeployKey getDeployKeyById(String id) throws DeployKeyNotFoundException {
    DeployKey deployKey = deployKeysTable.getItem(Key.builder().partitionValue(id).build());
    if (deployKey == null) {
      throw new DeployKeyNotFoundException(id);
    }
    return deployKey;
  }

  public DeployKey getDeployKeyByValue(String value) throws DeployKeyNotFoundException {
    Collection<DeployKey> deployKeys = (Collection<DeployKey>) findDeployKeys("", 1, value).getEntities();
    if (deployKeys == null || deployKeys.isEmpty()) {
      throw new DeployKeyNotFoundException("Could not find matching key");
    }
    return deployKeys
      .stream()
      .filter(deployKey -> deployKey.getKey().equals(value)) // Ensure exact match
      .findFirst()
      .orElseThrow(() -> new DeployKeyNotFoundException("Could not find matching key"));
  }

  public void deleteDeployKey(String id) {
    deployKeysTable.deleteItem(Key.builder().partitionValue(id).build());
  }

  public PaginationDto findDeployKeys(String identifier, Integer limit, String terms) {
    String whereClause = "contains(#id, :term)"
        + "or contains(#key, :term)";
    Expression expression = Expression.builder()
        .expressionNames(Map.of(
            "#id", "id",
            "#key", "key"
            )
        )
        .expressionValues(Map.of(":term", AttributeValue.fromS(terms)))
        .expression(whereClause)
        .build();
    ScanEnhancedRequest scanEnhancedRequest = buildScanRequest(
        identifier,
        limit,
        terms,
        expression
    );
    Page<DeployKey> deployKeyPage = deployKeysTable.scan(scanEnhancedRequest)
        .stream().findFirst().orElse(null);
    if (deployKeyPage == null) {
      return new PaginationDto(Collections.EMPTY_LIST);
    }
    return new PaginationDto(deployKeyPage.items());
  }
}