package api;

import core.exceptions.DeployKeyNotFoundException;
import core.service.DeployKeyService;
import core.tapir.DeployKey;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import java.time.Instant;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestHTTPEndpoint(Management.class)
@TestSecurity(authorizationEnabled = false)
public class ManagementTest {

  @InjectMock
  DeployKeyService deployKeyService;

  @Test
  void getDeployKey() throws DeployKeyNotFoundException {
    DeployKey fake = new DeployKey("foo-bar", "fake");
    fake.setLastModifiedAt(Instant.parse("2023-01-01T00:00:00Z"));
    when(deployKeyService.getDeployKey(any())).thenReturn(fake);
    given().get("/deploykey/foo-bar")
        .then()
        .statusCode(200)
        .body(
            "\"id\"", is("foo-bar"),
            "\"key\"", is("fake"),
            "\"lastModifiedAt\"", is("2023-01-01T00:00:00Z")
        );
  }

  @Test
  void getInvalidDeployKey() throws DeployKeyNotFoundException {
    when(deployKeyService.getDeployKey(any())).thenThrow(new DeployKeyNotFoundException("foo-bar"));
    Response response = given().get("/deploykey/foo-bar");
    assertEquals(404,response.getStatusCode());
    assertTrue(response.getBody().asString().contains("DeployKey for foo-bar does not exists"));
  }
}
