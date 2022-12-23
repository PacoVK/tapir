package api;

import backend.ElasticSearchService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import util.TestDataBuilder;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestHTTPEndpoint(Reports.class)
class ReportsTest {
  @InjectMock
  ElasticSearchService searchService;

  @Test
  void getSecurityReportForModuleVersion() throws IOException {
    when(searchService.getReportByModuleVersion(any())).thenReturn(TestDataBuilder.getSastReportStub());
    given().
            when().get("/foo/bar/baz/security/0.0.1")
            .then()
            .statusCode(200)
            .body(is(JsonObject.mapFrom(TestDataBuilder.getSastReportStub()).encode()));
  }
}