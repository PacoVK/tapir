package api;

import api.dto.ModulePaginationDto;
import core.service.backend.SearchService;
import core.terraform.Module;
import io.quarkus.security.Authenticated;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Produces(MediaType.APPLICATION_JSON)
@Path("/search")
public class Search {

  SearchService searchService;

  public Search(Instance<SearchService> searchServiceInstance) {
    this.searchService = searchServiceInstance.get();
  }

  @GET
  public Response searchModule(
          @DefaultValue("0")
          @QueryParam("offset")Optional<Integer> offset,
          @DefaultValue("10")
          @QueryParam("limit")Optional<Integer> limit,
          @QueryParam("q")Optional<String> query
          ) throws Exception {
    ModulePaginationDto modulePaginationDto;
    if(query.isEmpty()){
      modulePaginationDto = searchService.getModulesByRange(offset.get(), limit.get());
    } else {
      modulePaginationDto = searchService.getModulesByRangeAndTerm(query.get(), offset.get(), limit.get());
    }
    return Response.ok(modulePaginationDto).build();
  }
}
