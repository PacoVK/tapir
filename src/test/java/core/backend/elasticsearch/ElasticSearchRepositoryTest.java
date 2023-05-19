package core.backend.elasticsearch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import jakarta.ws.rs.HttpMethod;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ElasticSearchRepositoryTest {

  RestClient restClient;
  ElasticSearchRepository repository;

  public ElasticSearchRepositoryTest(ElasticSearchRepository repository, RestClient restClient) {
    this.repository = repository;
    this.restClient = restClient;
  }

  @AfterEach
  void tearDown() throws IOException {
    restClient.performRequest(new Request(HttpMethod.DELETE, "/funky"));
  }

  @Test
  void createIndexIfNotExistsTest() throws IOException {
    Integer statusCode = repository.createIndexIfNotExists("funky");
    assertEquals(statusCode, 200);
    Integer statusCodeDuplicate = repository.createIndexIfNotExists("funky");
    assertEquals(statusCodeDuplicate, 200);
  }
}