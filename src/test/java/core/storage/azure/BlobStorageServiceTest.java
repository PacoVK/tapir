package core.storage.azure;

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
import org.junit.jupiter.api.Test;

@QuarkusTest
class BlobStorageServiceTest extends AbstractStorageTest {

  BlobServiceClient client;
  BlobContainerClient blobContainerClient;
  @ConfigProperty(name = "registry.storage.azure.blobConnectionString")
  String azureBlobConnectionString;
  @ConfigProperty(name = "registry.storage.azure.containerName")
  String containerName;
  BlobStorageService blobStorageService;

  public BlobStorageServiceTest(BlobStorageService blobStorageService) {
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
    blobContainerClient.getBlobClient(UPLOADED_MODULE_FILENAME).delete();
  }

  @Test
  void testUploadModule() throws URISyntaxException, StorageException {
    uploadModule();
    PagedIterable<BlobItem> items = blobContainerClient.listBlobs();
    assertEquals(items.stream().count(), 1);
  }
}