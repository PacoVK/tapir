package core.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.dto.PaginationDto;
import core.terraform.Module;
import extensions.core.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import util.TestDataBuilder;

public abstract class AbstractModuleBackendTest {

  TapirRepository repository;

  public AbstractModuleBackendTest(TapirRepository repository) {
    this.repository = repository;
  }

  @BeforeEach
  void setUp() throws Exception {
    repository.bootstrap();
  }

  @Test
  void ingestModuleData() throws Exception {
    Module module = new Module("foo", "bar", "baz", "0.0.1");
    repository.ingestModuleData(module);
    Module ingestedModule = repository.getModule(module.getId());
    assertTrue(new ReflectionEquals(module).matches(ingestedModule));
  }

  @Test
  void ingestSecurityScanResult() throws Exception {
    Module module = new Module("baz", "bar", "foo", "0.0.2");
    repository.ingestModuleData(module);
    Report report = TestDataBuilder.getReportStub(module);
    repository.ingestSecurityScanResult(report);
    Report ingestedReport = repository.getReportByModuleVersion(module);
    assertEquals(ingestedReport.getId(), "baz-bar-foo-0.0.2");
  }

  @Test
  void increaseDownloadCounter() throws Exception {
    Module module = new Module("three", "two", "one", "0.0.3");
    repository.ingestModuleData(module);
    repository.increaseDownloadCounter(module);
    repository.increaseDownloadCounter(module);
    Module ingestedModule = repository.getModule(module.getId());
    assertEquals(ingestedModule.getDownloads(), 2);
  }


  @Test
  void findModules() throws Exception {
    Module awsNetworkModule = new Module("fancy", "vpc", "aws", "0.0.1");
    Module azureNetworkModule = new Module("fancy", "vpc", "azure", "0.0.1");
    Module otherModule = new Module("fancy", "container", "aws", "0.0.1");
    repository.ingestModuleData(awsNetworkModule);
    repository.ingestModuleData(azureNetworkModule);
    repository.ingestModuleData(otherModule);
    PaginationDto searchWithLimit = repository.findModules("", 3, "vpc");
    PaginationDto searchWithinLimit = repository.findModules("", 5, "vpc");
    PaginationDto searchWithoutResult = repository.findModules("", 5, "google");
    PaginationDto searchAll = repository.findModules("", 5, "");
    PaginationDto searchWithNonExistingIdentifier = repository.findModules("fancy-vsdfsfdspc-aws", 5, "");
    assertEquals(searchWithLimit.getEntities().size(), 2);
    assertEquals(searchWithinLimit.getEntities().size(), 2);
    assertEquals(searchWithoutResult.getEntities().size(), 0);
    assertEquals(searchAll.getEntities().size(), 3);
    assertEquals(searchWithNonExistingIdentifier.getEntities().size(), 0);
  }

  @Test
  void getModuleVersions() throws Exception {
    Module module_1 = new Module("namespace", "name", "provider", "0.0.1");
    Module module_2 = new Module("namespace", "name", "provider", "1.0.2");
    Module module_3 = new Module("namespace", "name", "provider", "0.0.2");
    repository.ingestModuleData(module_1);
    repository.ingestModuleData(module_2);
    repository.ingestModuleData(module_3);
    Module moduleVersions = repository.getModule(module_1.getId());
    assertEquals(moduleVersions.getCurrentVersion(),"1.0.2");
    assertEquals(moduleVersions.getVersions().size(), 3);
  }
}
