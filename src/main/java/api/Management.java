package api;


import core.service.DeployKeyService;
import core.tapir.DeployKey;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
  @Path("/deploykey/{id}")
  public Response createDeployKey(String id) throws Exception {
    DeployKey deployKey = deployKeyService.createDeployKey(id);
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
