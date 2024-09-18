package api;


import core.exceptions.TapirException;
import core.service.DeployKeyService;
import core.tapir.DeployKey;
import core.tapir.DeployKeyScope;
import core.terraform.Module;
import core.upload.FormData;
import extensions.core.Report;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;

import java.util.Objects;

@RolesAllowed("admin")
@Path("/management")
@Produces(MediaType.APPLICATION_JSON)
public class Management {
  DeployKeyService deployKeyService;

  public Management(DeployKeyService deployKeyService) {
    this.deployKeyService = deployKeyService;
  }

  @DELETE
  @Path("/deploykey/{id}")
  public Response deleteDeployKey(String id) throws Exception {
    deployKeyService.deleteDeployKey(id);
    return Response
        .ok(new JsonObject().put("id", id))
        .build();
  }

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Path("/deploykey")
  public Response createDeployKey(  @RestForm String resourceType,
  @RestForm DeployKeyScope scope,
  @RestForm String namespace,
  @RestForm String source,
  @RestForm String provider,
  @RestForm String name,
  @RestForm String type) throws Exception {
    DeployKey deployKey;
    if (Objects.equals(resourceType, "module")) {
      deployKey = deployKeyService.createModuleDeployKey(scope, source, namespace, name, provider);
    } else if (Objects.equals(resourceType, "provider")) {
      deployKey = deployKeyService.createProviderDeployKey(scope, source, namespace, type);
    } else {
      throw new TapirException("Unknown resource type");
    }
    return Response
        .ok(deployKey)
        .build();
  }

  @PUT
  @Path("/deploykey/{id}")
  public Response updateDeployKey(String id) throws Exception {
    DeployKey deployKey = deployKeyService.updateDeployKey(id);
    return Response
        .ok(deployKey)
        .build();
  }

  @GET
  @Path("/deploykey/{id}")
  public Response getDeployKey(
      String id
  ) throws Exception {
    DeployKey deployKey = deployKeyService.getDeployKey(id);
    return Response
        .ok(deployKey)
        .build();
  }
}
