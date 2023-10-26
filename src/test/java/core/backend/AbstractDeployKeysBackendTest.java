package core.backend;

import api.dto.PaginationDto;
import core.tapir.DeployKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractDeployKeysBackendTest {

  TapirRepository repository;

  public AbstractDeployKeysBackendTest(TapirRepository repository) {
    this.repository = repository;
  }

  @BeforeEach
  void setUp() throws Exception {
    repository.bootstrap();
  }

  @Test
  void saveAndUpdateDeployKey() throws Exception {
    DeployKey deployKey = new DeployKey("mae", "son");
    repository.saveDeployKey(deployKey);
    DeployKey persistedDeployKey = repository.getDeployKeyById(deployKey.getId());
    assertEquals(deployKey.getId(), persistedDeployKey.getId());

    deployKey.setKey("changed");
    repository.updateDeployKey(deployKey);
    DeployKey updatedDeployKey = repository.getDeployKeyById(deployKey.getId());
    assertEquals(deployKey.getId(), persistedDeployKey.getId());
    assertEquals(updatedDeployKey.getKey(), "changed");
  }

  @Test
  void findDeployKeys() throws Exception {
    DeployKey deployKey1 = new DeployKey("foo", "bar");
    DeployKey deployKey2 = new DeployKey("fred", "flintstone");

    repository.saveDeployKey(deployKey1);
    repository.saveDeployKey(deployKey2);
    PaginationDto noIdentifier = repository.findDeployKeys("", 5, "fred");
    PaginationDto withIdentifier = repository.findDeployKeys(deployKey1.getId(), 5, "");
    PaginationDto all = repository.findDeployKeys("", 5, "");
    assertEquals(noIdentifier.getEntities().size(), 1);
    assertEquals(withIdentifier.getEntities().size(), 1);
    assertEquals(all.getEntities().size(), 2);
  }

  @Test
  void findDeployKeysIfNoDeployKeysAreStored() throws Exception {
    PaginationDto result = repository.findDeployKeys("", 5, "fred");
    assertEquals(result.getEntities().size(), 0);
  }
}
