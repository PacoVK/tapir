package api;

import api.dto.ProviderTerraformDto;
import api.dto.ProviderVersionsDto;
import api.dto.mapper.ProviderMapper;
import api.dto.mapper.ProviderVersionMapper;
import core.backend.SearchService;
import core.exceptions.PlatformNotFoundException;
import core.storage.StorageService;
import core.terraform.ArtifactVersion;
import core.terraform.Provider;
import core.terraform.ProviderPlatform;
import core.upload.FormData;
import core.upload.service.UploadService;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.inject.Instance;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Produces(MediaType.APPLICATION_JSON)
@Path("/terraform/providers/v1/")
public class Providers {

  StorageService storageService;
  SearchService searchService;
  UploadService uploadService;
  EventBus eventBus;
  ProviderMapper providerMapper;
  ProviderVersionMapper providerVersionMapper;

  public Providers(
          Instance<StorageService> storageServiceInstance,
          Instance<SearchService> searchServiceInstance,
          UploadService uploadService,
          ProviderMapper providerMapper,
          ProviderVersionMapper providerVersionMapper,
          EventBus eventBus
  ) {
    this.storageService = storageServiceInstance.get();
    this.searchService = searchServiceInstance.get();
    this.uploadService = uploadService;
    this.providerMapper = providerMapper;
    this.providerVersionMapper = providerVersionMapper;
    this.eventBus = eventBus;
  }

  @GET
  @Path("/{namespace}/{type}/versions")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAvailableVersionsForProvider(String namespace, String type) throws Exception {
    Provider provider = new Provider(namespace, type);
    Provider providerById = searchService.getProviderById(provider.getId());
    List<ProviderVersionsDto> versionsDtos = providerVersionMapper.toDto(providerById);
    JsonObject jsonObject = new JsonObject()
            .put("versions", versionsDtos);
    return Response.ok(jsonObject).build();
  }

  @GET
  @Path("/{namespace}/{type}/{version}/download/{os}/{arch}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getDownloadUrl(
          String namespace,
          String type,
          String version,
          String os,
          String arch
  ) throws Exception {
    Provider provider = new Provider(namespace, type);
    Provider providerById = searchService.getProviderById(provider.getId());
    try {
      List<ProviderPlatform> platforms = providerById
              .getVersions()
              .get(new ArtifactVersion(version));
      ProviderPlatform providerPlatform = platforms.stream()
              .filter(platform -> platform.getOs().equals(os) && platform.getArch().equals(arch))
              .findFirst()
              .orElseThrow();
      ProviderTerraformDto dto = providerMapper
              .toDtoByVersionAndPlatform(providerById, version, providerPlatform);
      return Response.ok(dto).build();
    } catch (NoSuchElementException | NullPointerException exception) {
      throw new PlatformNotFoundException(version, exception);
    }
  }

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Path("{namespace}/{type}/{version}")
  public Response uploadProvider(String namespace, String type, String version, FormData archive)
          throws Exception {
    Provider provider = new Provider(namespace, type);
    provider.setPublished_at(Instant.now());
    archive.setEntity(provider);
    uploadService.uploadProvider(archive, version);
    return Response.ok().build();
  }

  @GET
  @Path("{namespace}/{type}")
  public Response getModuleByName(String namespace, String type) throws Exception {
    Provider provider = new Provider(namespace, type);
    return Response.ok(searchService.getProviderById(provider.getId())).build();
  }

}
