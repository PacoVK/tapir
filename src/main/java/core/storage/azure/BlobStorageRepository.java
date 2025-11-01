package core.storage.azure;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import core.exceptions.StorageException;
import core.storage.StorageRepository;
import core.storage.util.StorageUtil;
import core.terraform.Module;
import core.terraform.Provider;
import core.upload.FormData;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.stream.Stream;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@LookupIfProperty(name = "registry.storage.backend", stringValue = "azureBlob")
@ApplicationScoped
public class BlobStorageRepository extends StorageRepository {

  BlobServiceClient client;
  BlobContainerClient blobContainerClient;
  @ConfigProperty(name = "registry.storage.azure.blobConnectionString")
  String azureBlobConnectionString;
  @ConfigProperty(name = "registry.storage.azure.containerName")
  String containerName;

  @PostConstruct
  public void initialize() {
    this.client = new BlobServiceClientBuilder()
            .connectionString(azureBlobConnectionString)
            .buildClient();
    this.blobContainerClient = client.getBlobContainerClient(containerName);
  }

  @Override
  public void uploadModule(FormData archive) throws StorageException {
    Module module = archive.getEntity();
    String blobName = StorageUtil.generateModuleStoragePath(module);
    try {
      BlobClient blobClient = blobContainerClient
              .getBlobClient(blobName);
      blobClient.uploadFromFile(archive.getPayload().getPath(), true);
    } catch (Exception ex) {
      throw new StorageException(archive.getEntity().getId(), ex);
    }
  }

  @Override
  public String getDownloadUrlForArtifact(String path) {
    BlobClient blobClient = blobContainerClient.getBlobClient(path);
    OffsetDateTime keyExpiry = OffsetDateTime.now().plusMinutes(getAccessSessionDuration());

    BlobContainerSasPermission blobContainerSas = new BlobContainerSasPermission();
    blobContainerSas.setReadPermission(true);
    BlobServiceSasSignatureValues blobServiceSasSignatureValues =
            new BlobServiceSasSignatureValues(keyExpiry, blobContainerSas);
    String generatedSas = blobClient.generateSas(blobServiceSasSignatureValues);
    return blobClient.getBlobUrl()
            + "?archive=zip&"
            + generatedSas;
  }

  @Override
  public void uploadProvider(FormData archive, String version) throws StorageException {
    Provider provider = archive.getEntity();
    String blobPrefixPath = StorageUtil.generateProviderStorageDirectory(provider, version);
    try (Stream<Path> stream = Files.walk(archive.getCompressedFile().getParentFile().toPath())) {
      stream.filter(
                      path -> path.toFile().isFile()
                              && !path.toString().endsWith(".tmp"))
              .forEach(pathToFile -> {
                String blobName = blobPrefixPath
                        + "/"
                        + pathToFile.getFileName();
                uploadFile(blobName, pathToFile.toString());
              });
    } catch (Exception ex) {
      throw new StorageException(archive.getEntity().getId(), ex);
    }
  }

  void uploadFile(String blobName, String sourcePath) {
    BlobClient blobClient = blobContainerClient
            .getBlobClient(blobName);
    blobClient.uploadFromFile(sourcePath, true);
  }

  @Override
  public void checkHealth() throws Exception {
    // Verify Azure Blob connectivity by checking if container exists
    if (blobContainerClient == null) {
      throw new IllegalStateException("Azure Blob container client not initialized");
    }
    if (!blobContainerClient.exists()) {
      throw new IllegalStateException(
          "Azure Blob container does not exist: " + containerName);
    }
  }
}
