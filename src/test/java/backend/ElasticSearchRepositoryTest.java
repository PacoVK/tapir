package backend;

import static org.junit.jupiter.api.Assertions.*;

import api.dto.ModulePagination;
import core.backend.elasticsearch.ElasticSearchRepository;
import core.exceptions.ModuleNotFoundException;
import core.exceptions.ReportNotFoundException;
import core.terraform.Module;
import extensions.core.Report;
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
class ElasticSearchRepositoryTest {

  @Inject
  ElasticSearchRepository repository;

  @Inject
  RestClient restClient;

  @BeforeAll
  void setUp() throws IOException {
    repository.bootstrap();
  }

  @AfterAll
  void tearDown() throws IOException {
    restClient.performRequest(new Request(HttpMethod.DELETE, "/modules"));
    restClient.performRequest(new Request(HttpMethod.DELETE, "/reports"));
  }
  @Test
  void ingestModuleData() throws IOException, ModuleNotFoundException {
    Module module = new Module("foo", "bar", "baz", "0.0.1");
    repository.ingestModuleData(module);
    Module ingestedModule = repository.getModuleById(module.getId());
    assertTrue(new ReflectionEquals(module).matches(ingestedModule));
  }

  @Test
  void ingestSecurityScanResult() throws IOException, ReportNotFoundException {
    Module module = new Module("baz", "bar", "foo", "0.0.2");
    repository.ingestModuleData(module);
    Report report = TestDataBuilder.getReportStub(module);
    repository.ingestSecurityScanResult(report);
    Report ingestedReport = repository.getReportByModuleVersion(module);
    assertEquals(ingestedReport.getId(), "baz-bar-foo-0.0.2");
  }

  @Test
  void increaseDownloadCounter() throws IOException, ModuleNotFoundException {
    Module module = new Module("three", "two", "one", "0.0.3");
    repository.ingestModuleData(module);
    repository.increaseDownloadCounter(module);
    repository.increaseDownloadCounter(module);
    Module ingestedModule = repository.getModuleById(module.getId());
    assertEquals(ingestedModule.getDownloads(), 2);
  }

  @Test
  void findModules() throws IOException, InterruptedException {
    Module awsNetworkModule = new Module("fancy", "vpc", "aws", "0.0.1");
    Module azureNetworkModule = new Module("fancy", "vpc", "azure", "0.0.1");
    Module otherModule = new Module("fancy", "container", "aws", "0.0.1");
    repository.ingestModuleData(awsNetworkModule);
    repository.ingestModuleData(azureNetworkModule);
    repository.ingestModuleData(otherModule);
    TimeUnit.SECONDS.sleep(5);
    ModulePagination searchWithLimit = repository.findModules("", 1, "vpc");
    ModulePagination searchWithinLimit = repository.findModules("", 5, "vpc");
    ModulePagination searchWithoutResult = repository.findModules("", 5, "google");
    assertEquals(searchWithLimit.getModules().size(), 1);
    assertEquals(searchWithinLimit.getModules().size(), 2);
    assertEquals(searchWithoutResult.getModules().size(), 0);
  }

  @Test
  void getModuleVersions() throws IOException, ModuleNotFoundException {
    Module module_1 = new Module("namespace", "name", "provider", "0.0.1");
    Module module_2 = new Module("namespace", "name", "provider", "1.0.2");
    Module module_3 = new Module("namespace", "name", "provider", "0.0.2");
    repository.ingestModuleData(module_1);
    repository.ingestModuleData(module_2);
    repository.ingestModuleData(module_3);
    Module moduleVersions = repository.getModuleVersions(module_1);
    assertEquals(moduleVersions.getCurrentVersion(),"1.0.2");
    assertEquals(moduleVersions.getVersions().size(), 3);
  }
}