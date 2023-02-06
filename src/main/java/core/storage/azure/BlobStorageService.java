package core.storage.azure;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import core.exceptions.StorageException;
import core.storage.StorageService;
import core.storage.util.StorageUtil;
import core.terraform.Module;
import core.terraform.Provider;
import core.upload.FormData;
import io.quarkus.arc.lookup.LookupIfProperty;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@LookupIfProperty(name = "registry.storage.backend", stringValue = "azureBlob")
@ApplicationScoped
public class BlobStorageService extends StorageService {

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
  public void uploadModule(FormData archive) {
    Module module = archive.getEntity();
    String blobName = StorageUtil.generateModuleStoragePath(module);
    BlobClient blobClient = blobContainerClient
            .getBlobClient(blobName);
    blobClient.uploadFromFile(archive.getPayload().getPath(), true);
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
    return new StringBuilder(blobClient.getBlobUrl())
            .append("?")
            .append("archive=zip")
            .append("&")
            .append(generatedSas).toString();
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
                String blobName = new StringBuilder(blobPrefixPath)
                        .append("/")
                        .append(pathToFile.getFileName())
                        .toString();
                uploadFile(blobName, pathToFile.toString());
              });
    } catch (Exception ex) {
      throw new StorageException(((Provider) archive.getEntity()).getId(), ex);
    }
  }

  void uploadFile(String blobName, String sourcePath) {
    BlobClient blobClient = blobContainerClient
            .getBlobClient(blobName);
    blobClient.uploadFromFile(sourcePath, true);
  }
}
