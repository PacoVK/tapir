package storage.aws;

import core.service.storage.StorageService;
import core.service.upload.FormData;
import core.terraform.Module;
import io.quarkus.arc.lookup.LookupIfProperty;
import java.time.Duration;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import storage.util.StorageUtil;

@LookupIfProperty(name = "registry.storage.backend", stringValue = "s3")
@ApplicationScoped
public class S3StorageService extends StorageService {

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
    String s3ObjectKey = StorageUtil.generateModuleStoragePath(module);
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(s3ObjectKey)
            .build();
    GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
            .getObjectRequest(getObjectRequest)
            .signatureDuration(Duration.ofMinutes(getAccessSessionDuration()))
            .build();
    PresignedGetObjectRequest presignedGetObjectRequest = presigner
            .presignGetObject(getObjectPresignRequest);
    return String.format("s3::%s", presignedGetObjectRequest.url().toString());
  }

  @Override
  public void uploadModule(FormData archive) {
    s3.putObject(buildPutRequest(archive),
            RequestBody.fromFile(archive.getPayload()));
  }

  protected PutObjectRequest buildPutRequest(FormData archive) {
    Module module = archive.getModule();
    String s3ObjectKey = StorageUtil.generateModuleStoragePath(module);
    return PutObjectRequest.builder()
            .bucket(bucketName)
            .key(s3ObjectKey)
            .contentType(archive.getMimeType())
            .build();
  }
}
