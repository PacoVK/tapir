package exceptions;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import backend.DynamodbService;
import core.service.backend.SearchService;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import javax.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExceptionHandlerTest {
  @Inject
  DynamodbService services;

  @BeforeAll
  public void setup() {
    SearchService mock = Mockito.mock(DynamodbService.class);
    QuarkusMock.installMockForType(mock, SearchService.class);
  }

  @Test
  public void throwUnexpectedRuntimeExceptionInCustomerService() {
    Mockito.when(services.getModuleById(any())).thenThrow(new RuntimeException("Completely Unexpected"));
    Response errorResponse = given()
            .when()
            .get("/terraform/modules/v1/namespace/name/provider")
            .then()
            .statusCode(500)
            .extract().response();
    String errors = errorResponse.body().jsonPath().getList("errors").get(0).toString();
    assertEquals(
            errors,
            "{message=An unexpected error has occurred. Please raise an issue if you think this is a bug.}"
    );
  }
}