package api;

import api.dto.ProviderTerraformDto;
import api.dto.ProviderVersionsDto;
import core.service.ProviderService;
import core.terraform.Provider;
import core.upload.FormData;
import core.upload.service.UploadService;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Path("/terraform/providers/v1/")
public class Providers {

  ProviderService providerService;
  UploadService uploadService;
  EventBus eventBus;

  public Providers(
          ProviderService providerService,
          UploadService uploadService,
          EventBus eventBus
  ) {
    this.providerService = providerService;
    this.uploadService = uploadService;
    this.eventBus = eventBus;
  }

  @GET
  @Path("/{namespace}/{type}/versions")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAvailableVersionsForProvider(String namespace, String type) throws Exception {
    List<ProviderVersionsDto> versionsDtos = providerService
        .getAvailableVersionsForProvider(namespace, type);
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
    ProviderTerraformDto dto = providerService
        .getProviderByVersionAndPlatform(namespace, type, version, os, arch);
    return Response.ok(dto).build();
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
  public Response getProviderByName(String namespace, String type) throws Exception {
    Provider provider = new Provider(namespace, type);
    return Response.ok(providerService.getProvider(provider.getId())).build();
  }

}
