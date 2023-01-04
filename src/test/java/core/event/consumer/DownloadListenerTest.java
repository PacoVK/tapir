package core.event.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import backend.DynamodbService;
import core.terraform.Module;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import javax.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@QuarkusTest
class DownloadListenerTest {

  Module fakeModule = new Module("foo", "bar", "baz", "0.0.0");

  @Inject
  DynamodbService mock;

  @Inject
  DownloadListener dl;

  @Inject
  DynamoDbClient dynamoDbClient;


  @BeforeEach
  void setUp() {
    mock.bootstrap();
  }

  @AfterEach
  void tearDown() {
    DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    enhancedClient.table("Modules", null).deleteTable();
    enhancedClient.table("SecurityReports", null).deleteTable();
  }

  @Test
  void handleDownloadRequestedEvent() throws IOException {
    mock.ingestModuleData(fakeModule);
    assertEquals(fakeModule.getDownloads(), 0);
    fakeModule = dl.handleDownloadRequestedEvent(fakeModule);
    assertEquals(fakeModule.getDownloads(), 1);
  }
}