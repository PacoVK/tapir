package core.backend.aws.dynamodb.repository;

import core.backend.AbstractProviderBackendTest;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@QuarkusTest
class DynamodbProviderRepositoryTest extends AbstractProviderBackendTest {

  DynamodbRepository repository;
  DynamoDbClient dynamoDbClient;

  @ConfigProperty(name = "registry.search.dynamodb.tables.provider")
  String providerTableName;

  public DynamodbProviderRepositoryTest(DynamodbRepository repository, DynamoDbClient dynamoDbClient) {
    super(repository);
    this.repository = repository;
    this.dynamoDbClient = dynamoDbClient;
  }

  @AfterEach
  void tearDown() {
    DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    enhancedClient.table(providerTableName, null).deleteTable();
  }
}