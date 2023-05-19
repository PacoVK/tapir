package api;

import core.terraform.ServiceDiscovery;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Path("/.well-known")
public class Discovery {

  @GET
  @Path("/terraform.json")
  public Response getSupportedServices() {
    return Response.ok(new ServiceDiscovery()).build();
  }
}
