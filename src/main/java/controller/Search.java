package controller;

import core.service.backend.SearchService;
import core.terraform.Module;

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
          @QueryParam("q") String query,
          @DefaultValue("0")
          @QueryParam("offset")Optional<Integer> offset,
          @DefaultValue("0")
          @QueryParam("limit")Optional<Integer> limit,
          @QueryParam("provider")Optional<String> provider,
          @QueryParam("namespace")Optional<String> namespace,
          @QueryParam("verified")Optional<Boolean> verified
          ) throws Exception {
    Module mod = new Module();
    mod.setName("HelloWorld");
    if(provider.isPresent()){
      System.out.println(provider);
    } else {
      return Response.ok(searchService.getModuleByName(mod.getName())).build();
    }
    return Response.ok().build();
  }
}
