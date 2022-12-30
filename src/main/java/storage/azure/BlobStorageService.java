package storage.azure;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
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

@LookupIfProperty(name = "registry.storage.backend", stringValue = "azureBlob")
@ApplicationScoped
public class BlobStorageService extends StorageService {

  final DefaultAzureCredential defaultCredential = new DefaultAzureCredentialBuilder().build();
  BlobServiceClient client;
  BlobContainerClient blobContainerClient;
  @ConfigProperty(name = "registry.storage.azure.accountName")
  String storageAccountName;
  @ConfigProperty(name = "registry.storage.azure.blobEndpoint")
  String azureBlobEndpoint;
  @ConfigProperty(name = "registry.storage.azure.containerName")
  String containerName;

  @PostConstruct
  public void initialize() {
    String endpointUrl = String.format("%s/%s", azureBlobEndpoint, storageAccountName);
    this.client = new BlobServiceClientBuilder()
            .endpoint(endpointUrl)
            //.credential(defaultCredential)
            //.connectionString("DefaultEndpointsProtocol=https;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=https://127.0.0.1:10000/devstoreaccount1")
            .connectionString("DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1;")
            .buildClient();

    this.blobContainerClient = client.getBlobContainerClient(containerName);
  }

  @Override
  public void uploadModule(FormData archive) {
    Module module = archive.getModule();
    String blobName = new StringBuilder(module.getId())
            .append("-")
            .append(module.getCurrentVersion())
            .toString();
    BlobClient blobClient = blobContainerClient
            .getBlobClient(blobName);
    blobClient.uploadFromFile(archive.getPayload().getPath(), true);
  }

  @Override
  public String getDownloadUrlForModule(Module module) {
    String blobName = new StringBuilder(module.getId())
            .append("-")
            .append(module.getCurrentVersion())
            .toString();
    BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
    OffsetDateTime keyExpiry = OffsetDateTime.now().plusMinutes(5);

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
