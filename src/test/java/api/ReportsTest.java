package api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import core.backend.aws.dynamodb.repository.DynamodbRepository;
import core.exceptions.ReportNotFoundException;
import core.terraform.Module;
import extensions.core.Report;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import util.TestDataBuilder;

@QuarkusTest
@TestHTTPEndpoint(Reports.class)
class ReportsTest {

  final Module fakeModule = new Module("foo", "bar", "baz", "0.0.0");
  @InjectMock
  DynamodbRepository repository;

  @Test
  void getSecurityReportForModuleVersion() throws ReportNotFoundException {
    String fakeUrl = String.format("/%s/%s/%s/security/%s",
            fakeModule.getNamespace(),
            fakeModule.getName(),
            fakeModule.getProvider(),
            fakeModule.getCurrentVersion()
    );
    Report reportStub = TestDataBuilder.getReportStub(fakeModule);
    when(repository.getReportByModuleVersion(any())).thenReturn(reportStub);
    given().
            when().get(fakeUrl)
            .then()
            .statusCode(200)
            .body(is(JsonObject.mapFrom(reportStub).encode()));
  }
}