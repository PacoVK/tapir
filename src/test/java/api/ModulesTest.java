package api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import core.backend.aws.dynamodb.repository.DynamodbRepository;
import core.exceptions.ModuleNotFoundException;
import core.exceptions.StorageException;
import core.terraform.Module;
import core.terraform.ModuleVersion;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import core.storage.aws.S3StorageService;

@QuarkusTest
@TestHTTPEndpoint(Modules.class)
class ModulesTest {

  final Module fakeModule = new Module("foo", "bar", "baz", "0.0.0");

  @InjectMock
  DynamodbRepository repository;

  @InjectMock
  S3StorageService storageService;

  @BeforeEach
  void setUp() {
    this.fakeModule.setVersions(new TreeSet<>(
            Set.of(
                    new ModuleVersion("0.0.1"),
                    new ModuleVersion("0.0.2"),
                    new ModuleVersion("0.0.3")
            )
    ));
  }

  @Test
  void getAvailableVersionsForModule() throws ModuleNotFoundException {
    String fakeUrl = String.format("/%s/%s/%s/versions",
            fakeModule.getNamespace(),
            fakeModule.getName(),
            fakeModule.getProvider()
    );
    when(repository.getModuleVersions(any())).thenReturn(fakeModule);
    given().
            when().get(fakeUrl)
            .then()
            .statusCode(200)
            .body(is("{\"modules\":[{\"versions\":[{\"version\":\"0.0.3\"},{\"version\":\"0.0.2\"},{\"version\":\"0.0.1\"}]}]}"));
  }

  @Test
  void getDownloadUrl() throws StorageException {
    String fakeUrl = String.format("/%s/%s/%s/%s/download",
            fakeModule.getNamespace(),
            fakeModule.getName(),
            fakeModule.getProvider(),
            fakeModule.getCurrentVersion()
    );
    when(storageService.getDownloadUrlForModule(any())).thenReturn("s3://fakeurl");
    given().
            when().get(fakeUrl)
            .then()
            .statusCode(204)
            .header("X-Terraform-Get","s3://fakeurl");
  }
}