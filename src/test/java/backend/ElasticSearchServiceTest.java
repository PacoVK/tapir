package backend;

import static org.junit.jupiter.api.Assertions.*;

import api.dto.ModulePagination;
import core.terraform.Module;
import extensions.core.SastReport;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import util.TestDataBuilder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@QuarkusTest
class ElasticSearchServiceTest {

  @Inject
  ElasticSearchService service;

  @Inject
  RestClient restClient;

  @BeforeAll
  void setUp() throws IOException {
    service.bootstrap();
  }

  @AfterAll
  void tearDown() throws IOException {
    restClient.performRequest(new Request(HttpMethod.DELETE, "/modules"));
    restClient.performRequest(new Request(HttpMethod.DELETE, "/reports"));
  }
  @Test
  void ingestModuleData() throws IOException {
    Module module = new Module("foo", "bar", "baz", "0.0.1");
    service.ingestModuleData(module);
    Module ingestedModule = service.getModuleById(module.getId());
    assertTrue(new ReflectionEquals(module).matches(ingestedModule));
  }

  @Test
  void ingestSecurityScanResult() throws IOException {
    Module module = new Module("baz", "bar", "foo", "0.0.2");
    service.ingestModuleData(module);
    SastReport sastReport = TestDataBuilder.getSastReportStub(module);
    service.ingestSecurityScanResult(sastReport);
    SastReport ingestedReport = service.getReportByModuleVersion(module);
    assertEquals(ingestedReport.getId(), "baz-bar-foo-0.0.2");
  }

  @Test
  void increaseDownloadCounter() throws IOException {
    Module module = new Module("three", "two", "one", "0.0.3");
    service.ingestModuleData(module);
    service.increaseDownloadCounter(module);
    service.increaseDownloadCounter(module);
    Module ingestedModule = service.getModuleById(module.getId());
    assertEquals(ingestedModule.getDownloads(), 2);
  }

  @Test
  void findModules() throws IOException, InterruptedException {
    Module awsNetworkModule = new Module("fancy", "vpc", "aws", "0.0.1");
    Module azureNetworkModule = new Module("fancy", "vpc", "azure", "0.0.1");
    Module otherModule = new Module("fancy", "container", "aws", "0.0.1");
    service.ingestModuleData(awsNetworkModule);
    service.ingestModuleData(azureNetworkModule);
    service.ingestModuleData(otherModule);
    TimeUnit.SECONDS.sleep(5);
    ModulePagination searchWithLimit = service.findModules("", 1, "vpc");
    ModulePagination searchWithinLimit = service.findModules("", 5, "vpc");
    ModulePagination searchWithoutResult = service.findModules("", 5, "google");
    assertEquals(searchWithLimit.getModules().size(), 1);
    assertEquals(searchWithinLimit.getModules().size(), 2);
    assertEquals(searchWithoutResult.getModules().size(), 0);
  }

  @Test
  void getModuleVersions() throws IOException {
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