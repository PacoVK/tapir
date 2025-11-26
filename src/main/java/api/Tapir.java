package api;

import core.exceptions.ModuleNotFoundException;
import core.exceptions.ProviderNotFoundException;
import core.service.AuthService;
import core.storage.local.LocalStorageRepository;
import core.tapir.User;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Logger;

@Path("/tapir")
public class Tapir {

  static final Logger LOGGER = Logger.getLogger(Tapir.class.getName());

  AuthService authService;

  public Tapir(AuthService authService) {
    this.authService = authService;
  }

  @GET
  @Path("/storage/{namespace}/{name}/{identifier}/{filename}")
  public Response download(String namespace, String name, String identifier, String filename)
      throws ModuleNotFoundException, ProviderNotFoundException {
    String path = Paths.get(namespace, name, identifier, filename).toString();
    File artefact;
    if (identifier.matches(".*\\d.*")) {
      LOGGER.fine("Identifier is a version string, assume user requested a provider " + identifier);
      LOGGER.info("Requested the download of provider " + path);
      path = LocalStorageRepository.PROVIDER_RESOURCE_DIR + path;
      artefact = new File(path);
      if (!artefact.exists()) {
        throw new ProviderNotFoundException(path);
      }
    } else {
      LOGGER.info("Requested the download of module " + path);
      path = LocalStorageRepository.MODULE_RESOURCE_DIR + path;
      artefact = new File(path);
      if (!artefact.exists()) {
        throw new ModuleNotFoundException(path);
      }
    }

    // Determine content type based on file extension
    String contentType = "application/octet-stream";
    String lowerFilename = filename.toLowerCase();
    if (lowerFilename.endsWith(".zip")) {
      contentType = "application/zip";
    } else if (lowerFilename.endsWith(".sig")) {
      contentType = "application/pgp-signature";
    } else if (lowerFilename.endsWith("sha256sums") || lowerFilename.endsWith("sha256sums.sig")) {
      contentType = "text/plain";
    }

    return Response
            .ok(artefact)
            .type(contentType)
            .build();
  }

  @GET
  @Path("/user")
  public Response authenticate() {
    User currentUser = authService.getCurrentUser();
    return Response
            .ok(currentUser)
            .build();
  }
}
