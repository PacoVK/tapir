package storage.aws;

import core.service.upload.FormData;
import core.service.storage.StorageService;
import core.terraform.Module;
import io.quarkus.arc.lookup.LookupIfProperty;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.time.Duration;

@LookupIfProperty(name = "registry.storage.backend", stringValue = "s3")
@ApplicationScoped
public class S3StorageService extends StorageService{

  S3Client s3;

  @ConfigProperty(name = "registry.storage.s3.bucket.name")
  String bucketName;

  S3Presigner presigner;


  public S3StorageService(S3Client s3, S3Presigner presigner) {
    this.s3 = s3;
    this.presigner = presigner;
  }

  @Override
  public String getDownloadUrlForModule(Module module) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(String.format("%s/%s/%s/%s.zip", module.getNamespace(), module.getName(), module.getProvider(), module.getCurrentVersion())).build();
    GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder().getObjectRequest(getObjectRequest).signatureDuration(Duration.ofMinutes(120)).build();
    PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
    return String.format("s3::%s",presignedGetObjectRequest.url().toString());
  }

  @Override
  public Response.ResponseBuilder uploadModule(FormData archive) {
    PutObjectResponse putResponse = s3.putObject(buildPutRequest(archive),
            RequestBody.fromFile(archive.getPayload()));
    if (putResponse != null) {
      return Response.ok().status(Response.Status.CREATED);
    } else {
      return Response.serverError();
    }
  }

  protected PutObjectRequest buildPutRequest(FormData archive) {
    Module module = archive.getModule();
    return PutObjectRequest.builder()
            .bucket(bucketName)
            .key(String.format("%s/%s/%s/%s.zip", module.getNamespace(), module.getName(), module.getProvider(), module.getCurrentVersion()))
            .contentType(archive.getMimeType())
            .build();
  }
}
