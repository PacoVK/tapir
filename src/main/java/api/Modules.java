package api;

import core.backend.SearchService;
import core.exceptions.StorageException;
import core.storage.StorageService;
import core.storage.util.StorageUtil;
import core.terraform.Module;
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

@Produces(MediaType.APPLICATION_JSON)
@Path("/terraform/modules/v1")
public class Modules {

  StorageService storageService;
  SearchService searchService;
  UploadService uploadService;
  EventBus eventBus;

  public Modules(
          Instance<StorageService> storageServiceInstance,
          Instance<SearchService> searchServiceInstance,
          UploadService uploadService,
          EventBus eventBus
  ) {
    this.storageService = storageServiceInstance.get();
    this.searchService = searchServiceInstance.get();
    this.uploadService = uploadService;
    this.eventBus = eventBus;
  }

  @GET
  @Path("{namespace}/{name}/{provider}")
  public Response getModuleByName(String namespace, String name, String provider) throws Exception {
    Module module = new Module(namespace, name, provider);
    return Response.ok(searchService.getModuleById(module.getId())).build();
  }

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Path("{namespace}/{name}/{provider}/{version}")
  public Response uploadModule(
          String namespace, String name, String provider,
          String version, FormData archive) throws Exception {
    Module module = new Module(namespace, name, provider, version);
    module.setPublished_at(Instant.now());
    archive.setEntity(module);
    uploadService.uploadModule(archive);
    return Response.ok().build();
  }

  @GET
  @Path("{namespace}/{name}/{provider}/versions")
  public Response getAvailableVersionsForModule(
          String namespace, String name, String provider) throws Exception {
    Module module = searchService.getModuleVersions(new Module(namespace, name, provider));
    JsonObject jsonObject = new JsonObject()
            .put("modules", List.of(JsonObject.of("versions", module.getVersions())));
    return Response.ok(jsonObject).build();
  }

  @GET
  @Path("/{namespace}/{name}/{provider}/{version}/download")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getDownloadUrl(String namespace, String name, String provider, String version)
          throws StorageException {
    Module module = new Module(namespace, name, provider, version);
    String path = StorageUtil.generateModuleStoragePath(module);
    String signedUrl = storageService.getDownloadUrlForArtifact(path);
    String downloadUrl = String.format("s3::%s", signedUrl);
    eventBus.requestAndForget("module.download.requested", module);
    return Response.noContent().header("X-Terraform-Get", downloadUrl).build();
  }
}

