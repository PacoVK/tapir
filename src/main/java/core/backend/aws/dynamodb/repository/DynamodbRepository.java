package core.backend.aws.dynamodb.repository;

import api.dto.PaginationDto;
import core.backend.SearchService;
import core.exceptions.ModuleNotFoundException;
import core.exceptions.ProviderNotFoundException;
import core.exceptions.ReportNotFoundException;
import core.terraform.Module;
import core.terraform.Provider;
import extensions.core.Report;
import io.quarkus.arc.lookup.LookupIfProperty;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
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
public class DynamodbRepository extends SearchService {

  static final Logger LOGGER = Logger.getLogger(DynamodbRepository.class.getName());

  final DynamoDbTable<Module> modulesTable;
  final DynamoDbTable<Report> reportsTable;
  final DynamoDbTable<Provider> providerTable;
  final DynamoDbClient dynamoDbClient;
  final TableSchema<Provider> providerTableSchema = TableSchemas.providerTableSchema;
  final TableSchema<Module> moduleTableSchema = TableSchemas.moduleTableSchema;
  final TableSchema<Report> reportsTableSchema = TableSchemas.reportsTableSchema;

  public DynamodbRepository(DynamoDbClient dynamoDbClient) {
    this.dynamoDbClient = dynamoDbClient;
    DynamoDbEnhancedClient dbEnhancedClient = DynamoDbEnhancedClient
            .builder().dynamoDbClient(dynamoDbClient).build();
    this.modulesTable = dbEnhancedClient.table("Modules", moduleTableSchema);
    this.providerTable = dbEnhancedClient.table("Providers", providerTableSchema);
    this.reportsTable = dbEnhancedClient.table("Reports", reportsTableSchema);
  }

  @Override
  public Provider getProviderById(String id) throws Exception {
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
    String whereClause = new StringBuilder("contains(#namespace, :term)")
            .append("or contains(#type, :term)")
            .toString();
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
    String reportId = new StringBuilder(module.getId())
            .append("-").append(module.getCurrentVersion()).toString();
    return getReportById(reportId);
  }

  public void bootstrap() {
    createTable(modulesTable);
    createTable(providerTable);
    createTable(reportsTable);
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
              .waitUntilTableExists(builder -> builder.tableName("Modules").build())
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
    String whereClause = new StringBuilder("contains(#namespace, :term)")
            .append("or contains(#name, :term)")
            .append("or contains(#provider, :term)")
            .toString();
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
            .stream().findFirst().orElse(null);
    if (modulePage == null) {
      return new PaginationDto(Collections.EMPTY_LIST);
    }
    return new PaginationDto(modulePage.items());
  }

  public Module getModuleById(String name) throws ModuleNotFoundException {
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

  @Override
  public Module getModuleVersions(Module module) throws ModuleNotFoundException {
    return getModuleById(module.getId());
  }
}