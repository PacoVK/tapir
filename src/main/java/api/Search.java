package api;

import api.dto.PaginationDto;
import core.service.DeployKeyService;
import core.service.ModuleService;
import core.service.ProviderService;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Path("/search")
public class Search {

  ProviderService providerService;
  ModuleService moduleService;
  DeployKeyService deployKeyService;

  public Search(ProviderService providerService, ModuleService moduleService, DeployKeyService deployKeyService) {
    this.providerService = providerService;
    this.moduleService = moduleService;
    this.deployKeyService = deployKeyService;
  }

  @GET
  @Path("/modules")
  public Response listModules(
          @DefaultValue("10")
          @QueryParam("limit") Integer limit,
          @DefaultValue("")
          @QueryParam("q") String query,
          @DefaultValue("")
          @QueryParam("lastKey") String lastEvaluatedElementKey
  ) throws Exception {
    PaginationDto modulePagination = moduleService.getModules(
            lastEvaluatedElementKey,
            limit,
            query
    );
    return Response.ok(modulePagination).build();
  }

  @GET
  @Path("/providers")
  public Response listProviders(
          @DefaultValue("10")
          @QueryParam("limit") Integer limit,
          @DefaultValue("")
          @QueryParam("q") String query,
          @DefaultValue("")
          @QueryParam("lastKey") String lastEvaluatedElementKey
  ) throws Exception {
    PaginationDto providerPagination = providerService.getProviders(
            lastEvaluatedElementKey,
            limit,
            query
    );
    return Response.ok(providerPagination).build();
  }

  @GET
  @Path("/deploykeys")
  public Response listDeployKeys(
      @DefaultValue("10")
      @QueryParam("limit") Integer limit,
      @DefaultValue("")
      @QueryParam("q") String query,
      @DefaultValue("")
      @QueryParam("lastKey") String lastEvaluatedElementKey
  ) throws Exception {
    PaginationDto providerPagination = deployKeyService.listDeployKeys(
        lastEvaluatedElementKey,
        limit,
        query
    );
    return Response.ok(providerPagination).build();
  }
}
