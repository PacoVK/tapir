package api;

import backend.DynamodbService;
import core.terraform.Module;
import extensions.core.SastReport;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import util.TestDataBuilder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestHTTPEndpoint(Reports.class)
class ReportsTest {

  final Module fakeModule = new Module("foo", "bar", "baz", "0.0.0");
  @InjectMock
  DynamodbService searchService;

  @Test
  void getSecurityReportForModuleVersion() {
    String fakeUrl = String.format("/%s/%s/%s/security/%s",
            fakeModule.getNamespace(),
            fakeModule.getName(),
            fakeModule.getProvider(),
            fakeModule.getCurrentVersion()
    );
    SastReport sastReportStub = TestDataBuilder.getSastReportStub(fakeModule);
    when(searchService.getReportByModuleVersion(any())).thenReturn(sastReportStub);
    given().
            when().get(fakeUrl)
            .then()
            .statusCode(200)
            .body(is(JsonObject.mapFrom(sastReportStub).encode()));
  }
}