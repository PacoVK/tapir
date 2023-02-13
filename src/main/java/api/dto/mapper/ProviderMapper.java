package api.dto.mapper;

import api.dto.GpgPublicKey;
import api.dto.ProviderTerraformDto;
import core.config.SigningKeys;
import core.exceptions.TapirException;
import core.storage.StorageService;
import core.storage.util.StorageUtil;
import core.terraform.Provider;
import core.terraform.ProviderPlatform;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;

@ApplicationScoped
public class ProviderMapper {

  private static final String DOWNLOAD_URL = "DOWNLOAD_URL";
  private static final String SHASUM_FILE_URL = "SHASUM_FILE_URL";
  private static final String SHASUM_FILE_SIG_URL = "SHASUM_FILE_SIG_URL";

  StorageService storageService;
  SigningKeys signingKeys;

  public ProviderMapper(Instance<StorageService> storageServiceInstance, SigningKeys signingKeys) {
    this.storageService = storageServiceInstance.get();
    this.signingKeys = signingKeys;
  }

  public ProviderTerraformDto toDtoByVersionAndPlatform(
          Provider provider,
          String version,
          ProviderPlatform providerPlatform
  ) throws TapirException {
    Map<String, String> generatedUrls = generateUrls(provider, version, providerPlatform);
    ProviderTerraformDto dto = new ProviderTerraformDto(
            provider.getNamespace(), provider.getType()
    );
    dto.setVersion(version);
    dto.setOs(providerPlatform.getOs());
    dto.setArch(providerPlatform.getArch());
    dto.setProtocols(provider.getProtocols());
    dto.setShasum(providerPlatform.getShasum());
    dto.setFilename(providerPlatform.getFileName());
    dto.setDownload_url(generatedUrls.get(DOWNLOAD_URL));
    dto.setShasums_url(generatedUrls.get(SHASUM_FILE_URL));
    dto.setShasums_signature_url(generatedUrls.get(SHASUM_FILE_SIG_URL));
    List<GpgPublicKey> gpgPublicKeys = signingKeys
            .keys()
            .stream()
            .map(key -> new GpgPublicKey(key.id(), key.asciiArmor()))
            .toList();
    dto.setSigning_keys(gpgPublicKeys);
    return dto;
  }

  Map<String, String> generateUrls(
          Provider provider,
          String version,
          ProviderPlatform providerPlatform
  ) throws TapirException {
    String basePath = StorageUtil.generateProviderStorageDirectory(provider, version);
    String fileName = providerPlatform.getFileName();
    if (!fileName.contains(version)) {
      throw new TapirException("Version mismatch. Provider file does not match registry version");
    }
    String providerPath = basePath
            + "/"
            + fileName;
    String downloadUrl = storageService.getDownloadUrlForArtifact(providerPath);
    String filePrefix = fileName
            .subSequence(0, fileName.lastIndexOf(version) + version.length())
            .toString();
    String shasumFilePath = basePath
            + "/"
            + filePrefix
            + "_"
            + "SHA256SUMS";
    String shaSumUrl = storageService.getDownloadUrlForArtifact(shasumFilePath);
    String shasumSigFilePath = shasumFilePath + ".sig";
    String shaSumSigUrl = storageService.getDownloadUrlForArtifact(shasumSigFilePath);
    return Map.of(
            DOWNLOAD_URL, downloadUrl,
            SHASUM_FILE_URL, shaSumUrl,
            SHASUM_FILE_SIG_URL, shaSumSigUrl
    );
  }
}
