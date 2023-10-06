package core.service;

import api.dto.GpgPublicKey;
import api.dto.PaginationDto;
import api.dto.ProviderTerraformDto;
import api.dto.ProviderVersionsDto;
import core.backend.TapirRepository;
import core.config.SigningKeys;
import core.exceptions.PlatformNotFoundException;
import core.exceptions.TapirException;
import core.storage.util.StorageUtil;
import core.terraform.ArtifactVersion;
import core.terraform.Provider;
import core.terraform.ProviderPlatform;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@ApplicationScoped
public class ProviderService {

  private static final String DOWNLOAD_URL = "DOWNLOAD_URL";
  private static final String SHASUM_FILE_URL = "SHASUM_FILE_URL";
  private static final String SHASUM_FILE_SIG_URL = "SHASUM_FILE_SIG_URL";

  TapirRepository tapirRepository;

  StorageService storageService;

  SigningKeys signingKeys;

  public ProviderService(Instance<TapirRepository> tapirRepositoryInstance,
                         StorageService storageService,
                         SigningKeys signingKeys) {
    this.tapirRepository = tapirRepositoryInstance.get();
    this.storageService = storageService;
    this.signingKeys = signingKeys;
  }

  public PaginationDto getProviders(String identifier, Integer limit, String terms) throws Exception {
    return tapirRepository.findProviders(identifier, limit, terms);
  }

  public void ingestProviderData(Provider provider) throws Exception {
    tapirRepository.ingestProviderData(provider);
  }

  public Provider getProvider(String id) throws Exception {
    return tapirRepository.getProvider(id);
  }

  public List<ProviderVersionsDto> getAvailableVersionsForProvider(String namespace, String type) throws Exception {
    Provider provider = getProvider(new Provider(namespace, type).getId());
    return buildProviderVersionsDto(provider);
  }

  public ProviderTerraformDto getProviderByVersionAndPlatform(
      String namespace,
      String type,
      String version,
      String os,
      String arch
  ) throws Exception {
    Provider provider = getProvider(new Provider(namespace, type).getId());
    try {
      List<ProviderPlatform> platforms = provider
          .getVersions()
          .get(new ArtifactVersion(version));
      ProviderPlatform providerPlatform = platforms.stream()
          .filter(platform -> platform.getOs().equals(os) && platform.getArch().equals(arch))
          .findFirst()
          .orElseThrow();
      return toDtoByVersionAndPlatform(provider, version, providerPlatform);
    } catch (NoSuchElementException | NullPointerException exception) {
      throw new PlatformNotFoundException(version, exception);
    }
  }

  List<ProviderVersionsDto> buildProviderVersionsDto(Provider provider) {
    List<ProviderVersionsDto> versionsDtos = new LinkedList<>();
    provider.getVersions().forEach(
        (artifactVersion, platforms) ->
            versionsDtos.add(
                new ProviderVersionsDto(artifactVersion.getVersion(), platforms)
            )
    );
    return versionsDtos;
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
