package api;

import api.dto.PaginationDto;
import core.backend.SearchService;
import javax.enterprise.inject.Instance;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
