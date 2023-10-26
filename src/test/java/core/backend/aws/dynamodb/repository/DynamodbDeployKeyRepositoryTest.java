package core.backend.aws.dynamodb.repository;

import core.backend.AbstractDeployKeysBackendTest;
import core.tapir.DeployKey;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

@QuarkusTest
class DynamodbDeployKeyRepositoryTest extends AbstractDeployKeysBackendTest {

  DynamodbRepository repository;
  DynamoDbClient dynamoDbClient;

  public DynamodbDeployKeyRepositoryTest(DynamodbRepository repository, DynamoDbClient dynamoDbClient) {
    super(repository);
    this.repository = repository;
    this.dynamoDbClient = dynamoDbClient;
  }

  @AfterEach
  void tearDown() {
    DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    enhancedClient.table("DeployKeys", null).deleteTable();
  }

  @Test
  void cannotOverwriteDeployKey() throws Exception {
    DeployKey deployKey = new DeployKey("double", "dcore");
    repository.saveDeployKey(deployKey);
    DeployKey persistedDeployKey = repository.getDeployKeyById(deployKey.getId());
    persistedDeployKey.setKey("changed");
    assertThrows(ConditionalCheckFailedException.class, () -> repository.saveDeployKey(persistedDeployKey));
  }
}