package api;

import core.service.backend.SearchService;
import core.terraform.Module;
import extensions.core.SastReport;

import javax.enterprise.inject.Instance;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Produces(MediaType.APPLICATION_JSON)
@Path("/reports")
public class Reports {

  SearchService searchService;

  public Reports(Instance<SearchService> searchServiceInstance) {
    this.searchService = searchServiceInstance.get();
  }

  @GET
  @Path("{namespace}/{name}/{provider}/security/{moduleVersion}")
  public Response getSecurityReportForModuleVersion(String namespace, String name, String provider, String moduleVersion) throws IOException {
    Module module = new Module(namespace, name, provider, moduleVersion);
    SastReport sastReport = searchService.getReportByModuleVersion(module);
    return Response.ok(sastReport).build();
  }
}
