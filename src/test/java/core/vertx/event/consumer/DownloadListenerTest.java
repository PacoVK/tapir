package core.vertx.event.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import core.backend.aws.dynamodb.repository.DynamodbRepository;
import core.terraform.Module;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@QuarkusTest
class DownloadListenerTest {

  Module fakeModule = new Module("foo", "bar", "baz", "0.0.0");

  @ConfigProperty(name = "registry.search.dynamodb.tables.modules")
  String moduleTableName;

  @ConfigProperty(name = "registry.search.dynamodb.tables.reports")
  String reportsTableName;

  @Inject
  DynamodbRepository repository;

  @Inject
  DownloadListener dl;

  @Inject
  DynamoDbClient dynamoDbClient;


  @BeforeEach
  void setUp() {
    repository.bootstrap();
  }

  @AfterEach
  void tearDown() {
    DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    enhancedClient.table(moduleTableName, null).deleteTable();
    enhancedClient.table(reportsTableName, null).deleteTable();
  }

  @Test
  void handleDownloadRequestedEvent() throws Exception {
    repository.ingestModuleData(fakeModule);
    assertEquals(fakeModule.getDownloads(), 0);
    fakeModule = dl.handleDownloadRequestedEvent(fakeModule);
    assertEquals(fakeModule.getDownloads(), 1);
  }
}