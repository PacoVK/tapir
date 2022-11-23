package controller;

import core.api.ServiceDiscovery;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Path("/.well-known")
public class Discovery {

  @GET
  @Path("/terraform.json")
  public Response getSupportedServices(){
    return Response.ok(new ServiceDiscovery()).build();
  }
}
