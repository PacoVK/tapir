package backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.dto.ModulePagination;
import core.terraform.Module;
import extensions.core.SastReport;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import util.TestDataBuilder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@QuarkusTest
class DynamodbServiceTest {

  @Inject
  DynamodbService service;

  @Inject
  DynamoDbClient dynamoDbClient;

  @BeforeAll
  void setUp() {
    service.bootstrap();
  }

  @AfterAll
  void tearDown() {
    DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    enhancedClient.table("Modules", null).deleteTable();
    enhancedClient.table("SecurityReports", null).deleteTable();
  }

  @Test
  @Order(1)
  void ingestModuleData() {
    Module module = new Module("foo", "bar", "baz", "0.0.1");
    service.ingestModuleData(module);
    Module ingestedModule = service.getModuleById(module.getId());
    assertTrue(new ReflectionEquals(module).matches(ingestedModule));
  }

  @Test
  void ingestSecurityScanResult() {
    Module module = new Module("baz", "bar", "foo", "0.0.2");
    service.ingestModuleData(module);
    SastReport sastReport = TestDataBuilder.getSastReportStub(module);
    service.ingestSecurityScanResult(sastReport);
    SastReport ingestedReport = service.getReportByModuleVersion(module);
    assertEquals(ingestedReport.getId(), "baz-bar-foo-0.0.2");
  }

  @Test
  void increaseDownloadCounter() {
    Module module = new Module("three", "two", "one", "0.0.3");
    service.ingestModuleData(module);
    service.increaseDownloadCounter(module);
    service.increaseDownloadCounter(module);
    Module ingestedModule = service.getModuleById(module.getId());
    assertEquals(ingestedModule.getDownloads(), 2);
  }


  // TODO fix this
  @Test
  @Disabled
  void findModules() {
    Module awsNetworkModule = new Module("fancy", "vpc", "aws", "0.0.1");
    Module azureNetworkModule = new Module("fancy", "vpc", "azure", "0.0.1");
    Module otherModule = new Module("fancy", "container", "aws", "0.0.1");
    service.ingestModuleData(awsNetworkModule);
    service.ingestModuleData(azureNetworkModule);
    service.ingestModuleData(otherModule);
    ModulePagination searchWithLimit = service.findModules("", 2, "vpc");
    ModulePagination searchWithinLimit = service.findModules("", 5, "vpc");
    ModulePagination searchWithoutResult = service.findModules("", 5, "google");
    ModulePagination searchAll = service.findModules("", 5, "");
    ModulePagination searchWithNonExistingIdentifier = service.findModules("fancy-vsdfsfdspc-aws", 5, "");
    assertEquals(searchWithLimit.getModules().size(), 1);
    assertEquals(searchWithinLimit.getModules().size(), 2);
    assertEquals(searchWithoutResult.getModules().size(), 0);
    assertEquals(searchAll.getModules().size(), 3);
    assertEquals(searchWithNonExistingIdentifier.getModules().size(), 0);
  }

  @Test
  void getModuleVersions() {
    Module module_1 = new Module("namespace", "name", "provider", "0.0.1");
    Module module_2 = new Module("namespace", "name", "provider", "1.0.2");
    Module module_3 = new Module("namespace", "name", "provider", "0.0.2");
    service.ingestModuleData(module_1);
    service.ingestModuleData(module_2);
    service.ingestModuleData(module_3);
    Module moduleVersions = service.getModuleVersions(module_1);
    assertEquals(moduleVersions.getCurrentVersion(),"1.0.2");
    assertEquals(moduleVersions.getVersions().size(), 3);
  }
}