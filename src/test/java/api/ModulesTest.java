package api;

import core.exceptions.StorageException;
import core.service.ModuleService;
import core.storage.aws.S3StorageRepository;
import core.terraform.ArtifactVersion;
import core.terraform.Module;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import java.util.Set;
import java.util.TreeSet;
import static org.hamcrest.CoreMatchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestHTTPEndpoint(Modules.class)
class ModulesTest {

  final Module fakeModule = new Module("foo", "bar", "baz", "0.0.0");

  @InjectMock
  ModuleService moduleService;

  @InjectMock
  S3StorageRepository storageService;

  @BeforeEach
  void setUp() {
    this.fakeModule.setVersions(new TreeSet<>(
            Set.of(
                    new ArtifactVersion("0.0.1"),
                    new ArtifactVersion("0.0.2"),
                    new ArtifactVersion("0.0.3")
            )
    ));
  }

  @Test
  void getAvailableVersionsForModule() throws Exception {
    String fakeUrl = String.format("/%s/%s/%s/versions",
            fakeModule.getNamespace(),
            fakeModule.getName(),
            fakeModule.getProvider()
    );
    when(moduleService.getModuleVersions(any())).thenReturn(fakeModule.getVersions());
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
    when(storageService.getDownloadUrlForArtifact(any())).thenReturn("https://fakeurl");
    given().
            when().get(fakeUrl)
            .then()
            .statusCode(204)
            .header("X-Terraform-Get","https://fakeurl");
  }
}