package storage.azure;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import core.service.storage.StorageService;
import core.service.upload.FormData;
import core.terraform.Module;
import io.quarkus.arc.lookup.LookupIfProperty;
import java.time.OffsetDateTime;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import storage.util.StorageUtil;

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
    Module module = archive.getModule();
    String blobName = StorageUtil.generateModuleStoragePath(module);
    BlobClient blobClient = blobContainerClient
            .getBlobClient(blobName);
    blobClient.uploadFromFile(archive.getPayload().getPath(), true);
  }

  @Override
  public String getDownloadUrlForModule(Module module) {
    String blobName = StorageUtil.generateModuleStoragePath(module);
    BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
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
}
