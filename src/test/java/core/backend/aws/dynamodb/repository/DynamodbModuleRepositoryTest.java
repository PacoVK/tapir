package core.backend.aws.dynamodb.repository;

import core.backend.AbstractModuleBackendTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
@QuarkusTest
class DynamodbModuleRepositoryTest extends AbstractModuleBackendTest {

  DynamodbRepository repository;
  DynamoDbClient dynamoDbClient;

  public DynamodbModuleRepositoryTest(DynamodbRepository repository, DynamoDbClient dynamoDbClient) {
    super(repository);
    this.repository = repository;
    this.dynamoDbClient = dynamoDbClient;
  }

  @AfterEach
  void tearDown() {
    DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    enhancedClient.table("Modules", null).deleteTable();
    enhancedClient.table("Reports", null).deleteTable();
  }
}