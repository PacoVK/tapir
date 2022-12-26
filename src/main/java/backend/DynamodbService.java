package backend;

import api.dto.ModulePagination;

import backend.aws.dynamodb.converter.ModuleVersionsConverter;
import backend.aws.dynamodb.converter.SecurityReportConverter;
import core.service.backend.SearchService;
import core.terraform.Module;
import extensions.core.SastReport;
import io.quarkus.arc.lookup.LookupIfProperty;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

@LookupIfProperty(name = "registry.search.backend", stringValue = "dynamodb")
@ApplicationScoped
public class DynamodbService extends SearchService {
  final DynamoDbTable<Module> modulesTable;
  final DynamoDbTable<SastReport> securityReportsTable;

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
                  .addAttribute(LinkedList.class, a -> a.name("versions")
                          .getter(Module::getVersions)
                          .setter(Module::setVersions)
                          .attributeConverter((AttributeConverter) new ModuleVersionsConverter()))
                  .addAttribute(Instant.class, a -> a.name("published_at")
                          .getter(Module::getPublished_at)
                          .setter(Module::setPublished_at))
                  .build();


  TableSchema<SastReport> sastReportTableSchema =
          TableSchema.builder(SastReport.class)
                  .newItemSupplier(SastReport::new)
                  .addAttribute(String.class, a -> a.name("id")
                          .getter(SastReport::getId)
                          .setter(SastReport::setId)
                          .tags(primaryPartitionKey()))
                  .addAttribute(String.class, a -> a.name("moduleName")
                          .getter(SastReport::getModuleName)
                          .setter(SastReport::setModuleName))
                  .addAttribute(String.class, a -> a.name("moduleNamespace")
                          .getter(SastReport::getModuleNamespace)
                          .setter(SastReport::setModuleNamespace))
                  .addAttribute(String.class, a -> a.name("provider")
                          .getter(SastReport::getProvider)
                          .setter(SastReport::setProvider))
                  .addAttribute(Map.class, a -> a.name("report")
                          .getter(SastReport::getSecurityReport)
                          .setter(SastReport::setSecurityReport)
                          .attributeConverter((AttributeConverter) new SecurityReportConverter())
                  )
                  .build();

  public DynamodbService(DynamoDbClient dynamoDbClient) {
    DynamoDbEnhancedClient dbEnhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    this.modulesTable = dbEnhancedClient.table("Modules", moduleTableSchema);
    this.securityReportsTable = dbEnhancedClient.table("SecurityReports", sastReportTableSchema);
  }

  @Override
  public void ingestModuleData(Module module) throws IOException {
    Module moduleToIngest;
    Module existingModule = modulesTable.getItem(module);
    if (existingModule != null) {
      existingModule.getVersions().add(module.getVersions().get(0));
      moduleToIngest = existingModule;
    } else {
      moduleToIngest = module;
    }
    modulesTable.updateItem(moduleToIngest);
  }

  @Override
  public void ingestSecurityScanResult(SastReport sastReport) {
    securityReportsTable.putItem(sastReport);
  }

  @Override
  public void increaseDownloadCounter(Module module) {
    Module existingModule = modulesTable.getItem(module);
    if (existingModule != null) {
      Integer downloads = existingModule.getDownloads();
      existingModule.setDownloads(downloads + 1);
    }
    modulesTable.updateItem(existingModule);
  }

  @Override
  public SastReport getReportByModuleVersion(Module module) {
    String reportId = new StringBuilder(module.getId()).append("-").append(module.getCurrentVersion()).toString();
    return securityReportsTable.getItem(Key.builder().partitionValue(reportId).build());
  }

  public void bootstrap() {
    try {
      modulesTable.createTable();
    } catch (ResourceInUseException inUseException){
      System.out.println("Table exists");
    }

    try {
      securityReportsTable.createTable();
    } catch (ResourceInUseException inUseException){
      System.out.println("Table exists");
    }

    System.out.println("Waiting for table creation...");

    try (DynamoDbWaiter waiter = DynamoDbWaiter.create()) {
      ResponseOrException<DescribeTableResponse> response = waiter
              .waitUntilTableExists(builder -> builder.tableName("Modules").build())
              .matched();
      DescribeTableResponse tableDescription = response.response().orElseThrow(
              () -> new RuntimeException("Customer table was not created."));
      System.out.println(tableDescription.table().tableName() + " was created.");
    } catch (Exception e){
      System.out.println("Foo");
    }
  }

  public void index(Module module) throws IOException {
    modulesTable.putItem(module);
  }

  public ModulePagination findModules(String identifier, Integer limit, String terms) {
    ScanEnhancedRequest.Builder scanEnhancedRequestBuilder = ScanEnhancedRequest.builder().limit(limit);
    if(!identifier.isEmpty()){
      scanEnhancedRequestBuilder.exclusiveStartKey(Map.of("id", AttributeValue.fromS(identifier)));
    }
    if(!terms.isEmpty()){
      Expression expression = Expression.builder()
              .expressionNames(Map.of(
                      "#namespace", "namespace",
                      "#name", "name",
                      "#provider", "provider")
              )
              .expressionValues(Map.of(":term", AttributeValue.fromS(terms)))
              .expression("contains(#namespace, :term) or contains(#name, :term) or contains(#provider, :term)")
              .build();
      scanEnhancedRequestBuilder.filterExpression(expression);
    }
    ScanEnhancedRequest scanEnhancedRequest =scanEnhancedRequestBuilder.build();
    Page<Module> modulePage = modulesTable.scan(scanEnhancedRequest).stream().findFirst().orElse(null);
    if(modulePage == null){
      return new ModulePagination(Collections.EMPTY_LIST);
    }
    return new ModulePagination(modulePage.items());
  }
  public Module getModuleById(String name) {
    return modulesTable.getItem(Key.builder().partitionValue(name).build());
  }

  @Override
  public Module getModuleVersions(Module module) {
    return getModuleById(module.getId());
  }
}