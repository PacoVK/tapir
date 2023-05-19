package core.vertx.event.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import core.backend.aws.dynamodb.repository.DynamodbRepository;
import core.terraform.Module;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@QuarkusTest
class DownloadListenerTest {

  Module fakeModule = new Module("foo", "bar", "baz", "0.0.0");

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
    enhancedClient.table("Modules", null).deleteTable();
    enhancedClient.table("Reports", null).deleteTable();
  }

  @Test
  void handleDownloadRequestedEvent() throws Exception {
    repository.ingestModuleData(fakeModule);
    assertEquals(fakeModule.getDownloads(), 0);
    fakeModule = dl.handleDownloadRequestedEvent(fakeModule);
    assertEquals(fakeModule.getDownloads(), 1);
  }
}