package core.backend.elasticsearch;

import core.backend.AbstractDeployKeysBackendTest;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.HttpMethod;
import java.io.IOException;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterEach;

@QuarkusTest
class ElasticSearchDeployKeyRepositoryTest extends AbstractDeployKeysBackendTest {

  RestClient restClient;
  public ElasticSearchDeployKeyRepositoryTest(ElasticSearchRepository repository, RestClient restClient) {
    super(repository);
    this.restClient = restClient;
  }
  @AfterEach
  void tearDown() throws IOException {
    restClient.performRequest(new Request(HttpMethod.DELETE, "/deploykeys"));
  }
}