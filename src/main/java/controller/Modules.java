package controller;

import core.service.upload.FormData;
import core.service.upload.UploadService;
import core.service.backend.SearchService;
import core.terraform.Module;
import core.terraform.ModuleVersion;
import io.vertx.core.json.JsonObject;

import javax.enterprise.inject.Instance;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Path("/terraform/modules/v1")
@Produces(MediaType.APPLICATION_JSON)
public class Modules {

  SearchService searchService;
  UploadService uploadService;

  public Modules(
          Instance<SearchService> searchServiceInstance,
          UploadService uploadService
  ) {
    this.searchService = searchServiceInstance.get();
    this.uploadService = uploadService;
  }

  @GET
  @Path("/")
  public Response listModules(
          @DefaultValue("0")
          @QueryParam("offset") Optional<Integer> offset,
          @DefaultValue("0")
          @QueryParam("limit")Optional<Integer> limit,
          @QueryParam("provider")Optional<String> provider,
          @QueryParam("verified")Optional<Boolean> verified
  ) throws Exception {
    Collection<Module> modules = searchService.getAllModules();
    return Response.ok(modules).build();
  }

  @GET
  @Path("/{namespace}")
  public Response listModulesByNameSpace(
          String namespace,
          @DefaultValue("0")
          @QueryParam("offset")Optional<Integer> offset,
          @DefaultValue("0")
          @QueryParam("limit")Optional<Integer> limit,
          @QueryParam("provider")Optional<String> provider,
          @QueryParam("verified")Optional<Boolean> verified
          ){
    Module m = new Module();
    m.setName(namespace);
    return Response.ok(m).build();
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
}
