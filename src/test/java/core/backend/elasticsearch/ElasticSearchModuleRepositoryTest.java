package core.backend.elasticsearch;

import core.backend.AbstractModuleBackendTest;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import javax.ws.rs.HttpMethod;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterEach;

@QuarkusTest
class ElasticSearchModuleRepositoryTest extends AbstractModuleBackendTest {

  RestClient restClient;

  public ElasticSearchModuleRepositoryTest(ElasticSearchRepository repository, RestClient restClient) {
    super(repository);
    this.restClient = restClient;
  }

  @AfterEach
  void tearDown() throws IOException {
    restClient.performRequest(new Request(HttpMethod.DELETE, "/modules"));
    restClient.performRequest(new Request(HttpMethod.DELETE, "/reports"));
  }
}