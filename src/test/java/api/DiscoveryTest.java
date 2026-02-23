package api;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(Discovery.class)
class DiscoveryTest {

  @Test
  void getSupportedServices() {
    given().get("/terraform.json")
            .then()
            .statusCode(200)
            .body(
                    "\"modules.v1\"", is("/terraform/modules/v1/"),
                    "\"providers.v1\"", is("/terraform/providers/v1/"),
                    "'login.v1'", notNullValue(),
                    "'login.v1'.client", is("tapir-cli"),
                    "'login.v1'.authz", is("http://localhost:8089/realms/tapir/protocol/openid-connect/auth"),
                    "'login.v1'.token", is("http://localhost:8089/realms/tapir/protocol/openid-connect/token"),
                    "'login.v1'.grant_types", hasItem("authz_code"),
                    "'login.v1'.ports", hasItems(10000, 10010)
            );
  }
}
