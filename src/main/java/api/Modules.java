package api;

import core.service.storage.StorageService;
import core.service.upload.FormData;
import core.service.upload.UploadService;
import core.service.backend.SearchService;
import core.terraform.Module;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;

import javax.enterprise.inject.Instance;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
  public Response uploadModule(String namespace, String name, String provider, String version, FormData archive) throws Exception {
    Module module = new Module(namespace, name, provider, version);
    module.setPublished_at(Instant.now());
    archive.setModule(module);
    uploadService.uploadModule(archive);
    return Response.ok().build();
  }

  @GET
  @Path("{namespace}/{name}/{provider}/versions")
  public Response getAvailableVersionsForModule(String namespace, String name, String provider) throws Exception {
    Module module = searchService.getModuleVersions(new Module(namespace, name, provider));
    JsonObject jsonObject = new JsonObject().put("modules", List.of(JsonObject.of("versions", module.getVersions())));
    return Response.ok(jsonObject).build();
  }

  @GET
  @Path("/{namespace}/{name}/{provider}/{version}/download")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getDownloadUrl(String namespace, String name, String provider, String version){
    Module module = new Module(namespace, name, provider, version);
    String downloadUrl = storageService.getDownloadUrlForModule(module);
    eventBus.requestAndForget("module.download.requested", module);
    return Response.noContent().header("X-Terraform-Get", downloadUrl).build();
  }
}

