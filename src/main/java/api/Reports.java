package api;

import core.backend.SearchService;
import core.exceptions.ReportNotFoundException;
import core.terraform.Module;
import extensions.core.Report;
import jakarta.enterprise.inject.Instance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
  public Response getSecurityReportForModuleVersion(
          String namespace,
          String name,
          String provider,
          String moduleVersion
  ) throws IOException, ReportNotFoundException {
    Module module = new Module(namespace, name, provider, moduleVersion);
    Report report = searchService.getReportByModuleVersion(module);
    return Response.ok(report).build();
  }
}
