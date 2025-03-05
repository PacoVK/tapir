package core.backend.elasticsearch;

import core.backend.AbstractModuleBackendTest;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import jakarta.ws.rs.HttpMethod;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@QuarkusTest
class ElasticSearchModuleRepositoryTest extends AbstractModuleBackendTest {

  RestClient restClient;
  @ConfigProperty(name = "registry.search.bucket.names.modules")
  String moduleIndexName;
  @ConfigProperty(name = "registry.search.bucket.names.reports")
  String reportsIndexName;

  public ElasticSearchModuleRepositoryTest(ElasticSearchRepository repository, RestClient restClient) {
    super(repository);
    this.restClient = restClient;
  }

  public String getModuleIndexName() {
    return moduleIndexName.toLowerCase().replaceAll("/[^a-z0-9]/", "");
  }

  public String getReportsIndexName() {
    return reportsIndexName.toLowerCase().replaceAll("/[^a-z0-9]/", "");
  }

  @AfterEach
  void tearDown() throws IOException {
    restClient.performRequest(new Request(HttpMethod.DELETE, String.format("/%s", moduleIndexName)));
    restClient.performRequest(new Request(HttpMethod.DELETE, String.format("/%s", reportsIndexName)));
  }
}