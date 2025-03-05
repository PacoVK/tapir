package core.backend.aws.dynamodb.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import core.backend.AbstractDeployKeysBackendTest;
import core.exceptions.DeployKeyNotFoundException;
import core.tapir.DeployKey;
import core.tapir.DeployKeyScope;
import io.quarkus.test.junit.QuarkusTest;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

@QuarkusTest
class DynamodbDeployKeyRepositoryTest extends AbstractDeployKeysBackendTest {

  DynamodbRepository repository;
  DynamoDbClient dynamoDbClient;
  @ConfigProperty(name = "registry.search.bucket.names.deployKeys")
  String deployKeyTableName;

  public DynamodbDeployKeyRepositoryTest(DynamodbRepository repository, DynamoDbClient dynamoDbClient) {
    super(repository);
    this.repository = repository;
    this.dynamoDbClient = dynamoDbClient;
  }

  @AfterEach
  void tearDown() {
    DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    enhancedClient.table(deployKeyTableName, null).deleteTable();
  }

  @Test
  void cannotOverwriteDeployKey() throws Exception {
    DeployKey deployKey = new DeployKey(DeployKeyScope.NAMESPACE, "", "double", "", "dcore");
    repository.saveDeployKey(deployKey);
    DeployKey persistedDeployKey = repository.getDeployKeyById(deployKey.getId());
    persistedDeployKey.setKey("changed");
    assertThrows(ConditionalCheckFailedException.class, () -> repository.saveDeployKey(persistedDeployKey));
  }

  @Test
  void testGetDeployKeyByValue() throws Exception {

    DeployKey deployKey = new DeployKey(DeployKeyScope.NAMESPACE, "", "double", "", "dcore");
    repository.saveDeployKey(deployKey);

    String deployKeyValue = deployKey.getKey();
    String deployKeyValueSub = deployKeyValue.substring(0, 4); // first 4 characters

    assertDoesNotThrow(() -> repository.getDeployKeyByValue(deployKeyValue));
    assertThrows(DeployKeyNotFoundException.class, () -> repository.getDeployKeyByValue(deployKeyValueSub));
    assertThrows(DeployKeyNotFoundException.class, () -> repository.getDeployKeyByValue("H0p3Fu11yf@k3k3y"));
  }
}
