package api;

import core.terraform.Login;
import core.terraform.api.ServiceDiscovery;
import org.eclipse.microprofile.config.inject.ConfigProperty;

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
