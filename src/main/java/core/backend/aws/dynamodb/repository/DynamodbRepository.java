package core.backend.aws.dynamodb.repository;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

import api.dto.ModulePagination;
import core.backend.SearchService;
import core.backend.aws.dynamodb.converter.ModuleVersionsConverter;
import core.backend.aws.dynamodb.converter.SecurityReportConverter;
import core.backend.aws.dynamodb.converter.TerraformDocumentationConverter;
import core.exceptions.ModuleNotFoundException;
import core.exceptions.ReportNotFoundException;
import core.terraform.Module;
import extensions.core.Report;
import extensions.docs.report.TerraformDocumentation;
import io.quarkus.arc.lookup.LookupIfProperty;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
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

  final DynamoDbClient dynamoDbClient;

  TableSchema<Module> moduleTableSchema =
          TableSchema.builder(Module.class)
                  .newItemSupplier(Module::new)
                  .addAttribute(String.class, a -> a.name("id")
                          .getter(Module::getId)
                          .setter(Module::setId)
                          .tags(primaryPartitionKey()))
                  .addAttribute(String.class, a -> a.name("name")
                          .getter(Module::getName)
                          .setter(Module::setName))
                  .addAttribute(String.class, a -> a.name("namespace")
                          .getter(Module::getNamespace)
                          .setter(Module::setNamespace))
                  .addAttribute(String.class, a -> a.name("provider")
                          .getter(Module::getProvider)
                          .setter(Module::setProvider))
                  .addAttribute(Integer.class, a -> a.name("downloads")
                          .getter(Module::getDownloads)
                          .setter(Module::setDownloads))
                  .addAttribute(TreeSet.class, a -> a.name("versions")
                          .getter(Module::getVersions)
                          .setter(Module::setVersions)
                          .attributeConverter((AttributeConverter) new ModuleVersionsConverter()))
                  .addAttribute(Instant.class, a -> a.name("published_at")
                          .getter(Module::getPublished_at)
                          .setter(Module::setPublished_at))
                  .build();


  TableSchema<Report> reportsTableSchema =
          TableSchema.builder(Report.class)
                  .newItemSupplier(Report::new)
                  .addAttribute(String.class, a -> a.name("id")
                          .getter(Report::getId)
                          .setter(Report::setId)
                          .tags(primaryPartitionKey()))
                  .addAttribute(String.class, a -> a.name("moduleName")
                          .getter(Report::getModuleName)
                          .setter(Report::setModuleName))
                  .addAttribute(String.class, a -> a.name("moduleNamespace")
                          .getter(Report::getModuleNamespace)
                          .setter(Report::setModuleNamespace))
                  .addAttribute(String.class, a -> a.name("provider")
                          .getter(Report::getProvider)
                          .setter(Report::setProvider))
                  .addAttribute(String.class, a -> a.name("moduleVersion")
                          .getter(Report::getModuleVersion)
                          .setter(Report::setModuleVersion))
                  .addAttribute(Map.class, a -> a.name("securityReport")
                          .getter(Report::getSecurityReport)
                          .setter(Report::setSecurityReport)
                          .attributeConverter((AttributeConverter) new SecurityReportConverter())
                  )
                  .addAttribute(TerraformDocumentation.class, a -> a.name("documentation")
                          .getter(Report::getDocumentation)
                          .setter(Report::setDocumentation)
                          .attributeConverter(new TerraformDocumentationConverter())
                  )
                  .build();

  public DynamodbRepository(DynamoDbClient dynamoDbClient) {
    this.dynamoDbClient = dynamoDbClient;
    DynamoDbEnhancedClient dbEnhancedClient = DynamoDbEnhancedClient
            .builder().dynamoDbClient(dynamoDbClient).build();
    this.modulesTable = dbEnhancedClient.table("Modules", moduleTableSchema);
    this.reportsTable = dbEnhancedClient.table("Reports", reportsTableSchema);
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

  public ModulePagination findModules(String identifier, Integer limit, String terms) {
    ScanEnhancedRequest.Builder scanEnhancedRequestBuilder = ScanEnhancedRequest.builder()
            .limit(limit);
    if (!identifier.isEmpty()) {
      scanEnhancedRequestBuilder.exclusiveStartKey(Map.of("id", AttributeValue.fromS(identifier)));
    }
    if (!terms.isEmpty()) {
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
      scanEnhancedRequestBuilder.filterExpression(expression);
    }
    ScanEnhancedRequest scanEnhancedRequest = scanEnhancedRequestBuilder.build();
    Page<Module> modulePage = modulesTable.scan(scanEnhancedRequest)
            .stream().findFirst().orElse(null);
    if (modulePage == null) {
      return new ModulePagination(Collections.EMPTY_LIST);
    }
    return new ModulePagination(modulePage.items());
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