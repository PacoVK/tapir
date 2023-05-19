package core.backend.elasticsearch;

import core.backend.AbstractProviderBackendTest;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import jakarta.ws.rs.HttpMethod;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterEach;

@QuarkusTest
class ElasticSearchProviderRepositoryTest extends AbstractProviderBackendTest {

  RestClient restClient;
  public ElasticSearchProviderRepositoryTest(ElasticSearchRepository repository, RestClient restClient) {
    super(repository);
    this.restClient = restClient;
  }
  @AfterEach
  void tearDown() throws IOException {
    restClient.performRequest(new Request(HttpMethod.DELETE, "/providers"));
  }
}