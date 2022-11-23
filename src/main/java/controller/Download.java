package controller;

import core.terraform.Module;
import core.service.storage.StorageService;

import javax.enterprise.inject.Instance;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/terraform/modules/v1")
public class Download {

  StorageService storageService;

  public Download(Instance<StorageService> storageServiceInstance) {
    this.storageService = storageServiceInstance.get();
  }

  @GET
  @Path("/{namespace}/{name}/{provider}/{version}/download")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getDownloadUrl(String namespace, String name, String provider, String version){
    Module module = new Module(namespace, name, provider, version);
    String downloadUrl = storageService.getDownloadUrlForModule(module);
    return Response.noContent().header("X-Terraform-Get", downloadUrl).build();
  }

  @GET
  @Path("/download")
  @Produces(MediaType.TEXT_HTML)
  public Response getDownloadUrlForLatest(){
    return Response.ok("<a href=\"/v1/modules/hashicorp/consul/aws/0.0.1/download\">Found</a>.").status(Response.Status.FOUND).build();
  }
}
