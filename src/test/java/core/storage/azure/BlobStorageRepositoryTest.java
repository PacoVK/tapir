package core.storage.azure;

import static io.smallrye.common.constraint.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import core.exceptions.StorageException;
import core.storage.AbstractStorageTest;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URISyntaxException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@QuarkusTest
class BlobStorageRepositoryTest extends AbstractStorageTest {

  BlobServiceClient client;
  BlobContainerClient blobContainerClient;
  @ConfigProperty(name = "registry.storage.azure.blobConnectionString")
  String azureBlobConnectionString;
  @ConfigProperty(name = "registry.storage.azure.containerName")
  String containerName;
  BlobStorageRepository blobStorageService;

  public BlobStorageRepositoryTest(BlobStorageRepository blobStorageService) {
    super(blobStorageService);
    this.blobStorageService = blobStorageService;
  }

  @BeforeEach
  void setUp() {
    this.client = new BlobServiceClientBuilder()
            .connectionString(azureBlobConnectionString)
            .buildClient();
    this.blobContainerClient = client.getBlobContainerClient(containerName);
  }

  @AfterEach
  void tearDown() {
    blobContainerClient.getBlobClient(UPLOADED_MODULE_FILENAME).deleteIfExists();
    blobContainerClient.getBlobClient("foo/bar/1.0.0/terraform_1.3.7_SHA256SUMS").deleteIfExists();
    blobContainerClient.getBlobClient("foo/bar/1.0.0/terraform_1.3.7_SHA256SUMS.sig").deleteIfExists();
    blobContainerClient.getBlobClient("foo/bar/1.0.0/terraform_1.3.7_SHA256SUMS.72D7468F.sig").deleteIfExists();
    blobContainerClient.getBlobClient("foo/bar/1.0.0/terraform_1.3.7_darwin_arm64.zip").deleteIfExists();
  }

  @Test
  void testUploadModule() throws URISyntaxException, StorageException {
    uploadModule();
    PagedIterable<BlobItem> items = blobContainerClient.listBlobs();
    assertEquals(items.stream().count(), 1);
  }

  @Test
  void testUploadProvider() throws URISyntaxException, StorageException {
    uploadProvider();
    PagedIterable<BlobItem> items = blobContainerClient.listBlobs();
    assertEquals(items.stream().count(), 4);
  }

  @Test
  @Disabled
  void testGetDownloadUrlForArtifact() throws StorageException {
    String url = getDownloadUrlForArtifact();
    assertTrue(url.startsWith("http://127.0.0.1:10000/devstoreaccount1/tf-registry/foo%2Fbar?archive=zip&sv="));
  }
}