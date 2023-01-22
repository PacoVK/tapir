package api;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestHTTPEndpoint(Discovery.class)
class DiscoveryTest {

  @Test
  void getSupportedServices() {
    given().get("/terraform.json")
            .then()
            .statusCode(200)
            .body(
                    "\"modules.v1\"", is("/terraform/modules/v1/")
            );
  }
}