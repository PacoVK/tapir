package core.storage.aws;

import static org.junit.jupiter.api.Assertions.assertEquals;

import core.exceptions.StorageException;
import core.storage.AbstractStorageTest;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URISyntaxException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@QuarkusTest
class S3StorageServiceTest extends AbstractStorageTest {

  S3StorageService s3StorageService;

  S3Client s3;

  @ConfigProperty(name = "registry.storage.s3.bucket.name")
  String bucketName;

  public S3StorageServiceTest(S3StorageService s3StorageService, S3Client s3) {
    super(s3StorageService);
    this.s3StorageService = s3StorageService;
    this.s3 = s3;
  }

  @AfterEach
  void tearDown() {
    DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
            .bucket(bucketName).delete(
                    Delete.builder().objects(
                            ObjectIdentifier.builder().key(UPLOADED_MODULE_FILENAME).build()).build()
            ).build();
    s3.deleteObjects(deleteObjectsRequest);
  }

  @Test
  void testUploadModule() throws URISyntaxException, StorageException {
    uploadModule();
    ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucketName).build();
    ListObjectsV2Response response = s3.listObjectsV2(listObjectsV2Request);
    assertEquals(response.keyCount(), 1);
  }
}