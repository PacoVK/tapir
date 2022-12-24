package api;

import backend.ElasticSearchService;
import core.terraform.Module;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import util.TestDataBuilder;

import java.io.IOException;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestHTTPEndpoint(Reports.class)
class ReportsTest {

  final Module fakeModule = new Module("foo", "bar", "baz", "0.0.0");
  @InjectMock
  ElasticSearchService searchService;

  @Test
  void getSecurityReportForModuleVersion() throws IOException {
    String fakeUrl = String.format("/%s/%s/%s/security/%s",
            fakeModule.getNamespace(),
            fakeModule.getName(),
            fakeModule.getProvider(),
            fakeModule.getCurrentVersion()
    );
    when(searchService.getReportByModuleVersion(any())).thenReturn(TestDataBuilder.getTrivyReportStub(fakeModule));
    given().
            when().get(fakeUrl)
            .then()
            .statusCode(200)
            .body(is(Objects.requireNonNull(JsonObject.mapFrom(TestDataBuilder.getTrivyReportStub(fakeModule))).encode()));
  }
}