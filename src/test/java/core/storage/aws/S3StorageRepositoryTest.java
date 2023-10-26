package core.storage.aws;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import core.exceptions.StorageException;
import core.storage.AbstractStorageTest;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URISyntaxException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@QuarkusTest
class S3StorageRepositoryTest extends AbstractStorageTest {

  S3StorageRepository s3StorageService;

  S3Client s3;

  @ConfigProperty(name = "registry.storage.s3.bucket.name")
  String bucketName;

  public S3StorageRepositoryTest(S3StorageRepository s3StorageService, S3Client s3) {
    super(s3StorageService);
    this.s3StorageService = s3StorageService;
    this.s3 = s3;
  }

  @AfterEach
  void tearDown() {
    DeleteObjectsRequest deleteModulesRequest = DeleteObjectsRequest.builder()
            .bucket(bucketName).delete(
                    Delete.builder().objects(
                            ObjectIdentifier.builder().key(UPLOADED_MODULE_FILENAME).build()).build()
            ).build();
    s3.deleteObjects(deleteModulesRequest);
    DeleteObjectsRequest deleteProvidersRequest = DeleteObjectsRequest.builder()
            .bucket(bucketName).delete(
                    Delete.builder().objects(
                            ObjectIdentifier.builder()
                                    .key("foo/bar/1.0.0/terraform_1.3.7_SHA256SUMS").build(),
                            ObjectIdentifier.builder()
                                    .key("foo/bar/1.0.0/terraform_1.3.7_SHA256SUMS.sig")
                                    .build(),
                            ObjectIdentifier.builder()
                                    .key("foo/bar/1.0.0/terraform_1.3.7_SHA256SUMS.72D7468F.sig")
                                    .build(),
                            ObjectIdentifier.builder()
                                    .key("foo/bar/1.0.0/terraform_1.3.7_darwin_arm64.zip")
                                    .build()
                            )
                            .build()
            ).build();
    s3.deleteObjects(deleteProvidersRequest);
  }

  @Test
  void testUploadModule() throws URISyntaxException, StorageException {
    uploadModule();
    ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucketName).build();
    ListObjectsV2Response response = s3.listObjectsV2(listObjectsV2Request);
    assertEquals(response.keyCount(), 1);
  }

  @Test
  void testUploadProvider() throws URISyntaxException, StorageException {
    uploadProvider();
    ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucketName).build();
    ListObjectsV2Response response = s3.listObjectsV2(listObjectsV2Request);
    assertEquals(response.keyCount(), 4);
  }

  @Test
  @Disabled
  void testGetDownloadUrlForArtifact() throws StorageException {
    String url = getDownloadUrlForArtifact();
    assertTrue(url.startsWith("http://localhost:4566/tf-registry/foo/bar?X-Amz-Security-Token="));
  }
}