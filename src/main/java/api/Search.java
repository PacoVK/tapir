package api;

import api.dto.PaginationDto;
import core.backend.SearchService;
import jakarta.enterprise.inject.Instance;
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

  SearchService searchService;

  public Search(Instance<SearchService> searchServiceInstance) {
    this.searchService = searchServiceInstance.get();
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
    PaginationDto modulePagination = searchService.findModules(
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
    PaginationDto providerPagination = searchService.findProviders(
            lastEvaluatedElementKey,
            limit,
            query
    );
    return Response.ok(providerPagination).build();
  }
}
