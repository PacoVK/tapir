package core.backend.elasticsearch;

import core.backend.AbstractProviderBackendTest;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import jakarta.ws.rs.HttpMethod;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@QuarkusTest
class ElasticSearchProviderRepositoryTest extends AbstractProviderBackendTest {

  RestClient restClient;
  @ConfigProperty(name = "registry.search.bucket.names.provider")
  String providerIndexName;

  public ElasticSearchProviderRepositoryTest(ElasticSearchRepository repository, RestClient restClient) {
    super(repository);
    this.restClient = restClient;
  }

  public String getProviderIndexName() {
    return providerIndexName.toLowerCase().replaceAll("/[^a-z0-9]/", "");
  }

  @AfterEach
  void tearDown() throws IOException {
    restClient.performRequest(new Request(HttpMethod.DELETE, String.format("/%s", providerIndexName)));
  }
}