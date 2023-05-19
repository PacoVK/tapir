package api;

import core.exceptions.ModuleNotFoundException;
import core.storage.local.LocalStorageService;
import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/tapir")
public class Tapir {

  static final Logger LOGGER = Logger.getLogger(Tapir.class.getName());

  @GET
  @Path("/storage/{namespace}/{name}/{identifier}/{filename}")
  @Produces("application/zip")
  public Response download(String namespace, String name, String identifier, String filename)
          throws ModuleNotFoundException {
    String path = Paths.get(namespace, name, identifier, filename).toString();
    if (identifier.matches(".*\\d.*")) {
      LOGGER.fine("Identifier is a version string, assume user requested a provider " + identifier);
      LOGGER.info("Requested the download of provider " + path);
      path = LocalStorageService.PROVIDER_RESOURCE_DIR + path;
    } else {
      LOGGER.info("Requested the download of module " + path);
      path = LocalStorageService.MODULE_RESOURCE_DIR + path;
    }
    File moduleArchive = new File(path);

    if (!moduleArchive.exists()) {
      throw new ModuleNotFoundException(path);
    }

    return Response
            .ok(moduleArchive)
            .build();
  }
}
