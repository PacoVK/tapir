package api;

import api.dto.PaginationDto;
import core.backend.SearchService;
import java.util.Optional;
import javax.enterprise.inject.Instance;
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
          @QueryParam("limit")Optional<Integer> limit,
          @QueryParam("q")Optional<String> query,
          @QueryParam("lastKey")Optional<String> lastEvaluatedElementKey
  ) throws Exception {
    PaginationDto modulePagination = searchService.findModules(
            lastEvaluatedElementKey.orElse(""),
            limit.orElse(10),
            query.orElse("")
    );
    return Response.ok(modulePagination).build();
  }

  @GET
  @Path("/providers")
  public Response listProviders(
          @QueryParam("limit")Optional<Integer> limit,
          @QueryParam("q")Optional<String> query,
          @QueryParam("lastKey")Optional<String> lastEvaluatedElementKey
  ) throws Exception {
    PaginationDto providerPagination = searchService.findProviders(
            lastEvaluatedElementKey.orElse(""),
            limit.orElse(10),
            query.orElse("")
    );
    return Response.ok(providerPagination).build();
  }
}
