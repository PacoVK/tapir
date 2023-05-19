package core.storage.local;

import core.exceptions.StorageException;
import core.storage.StorageService;
import core.storage.util.StorageUtil;
import core.upload.FormData;
import io.quarkus.arc.lookup.LookupIfProperty;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@LookupIfProperty(name = "registry.storage.backend", stringValue = "local")
@ApplicationScoped
public class LocalStorageService extends StorageService {

  static final Logger LOGGER = Logger.getLogger(LocalStorageService.class.getName());

  static final String BASE_RESOURCE_DIR = System.getProperty("user.home") + "/tapir/";

  public static final String MODULE_RESOURCE_DIR = BASE_RESOURCE_DIR + "/modules/";
  public static final String PROVIDER_RESOURCE_DIR = BASE_RESOURCE_DIR + "/providers/";

  @ConfigProperty(name = "registry.hostname")
  String hostName;

  @ConfigProperty(name = "registry.port")
  Integer port;


  @Override
  public void uploadModule(FormData archive) throws StorageException {
    Path moduleFile = Paths.get(
            MODULE_RESOURCE_DIR,
            StorageUtil.generateModuleStoragePath(archive.getEntity())
    );
    LOGGER.info("Persist module " + moduleFile);
    Path modulePath = moduleFile.getParent();
    try {
      if (!modulePath.toFile().exists()) {
        Files.createDirectories(moduleFile.getParent());
      }
      Files.copy(archive.getPayload().toPath(), moduleFile, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new StorageException(archive.getEntity().getId(), e);
    }
  }

  @Override
  public String getDownloadUrlForArtifact(String path) throws StorageException {
    return "https://" + hostName + ":" + port + "/tapir/storage/" + path;
  }

  @Override
  public void uploadProvider(FormData archive, String version) throws StorageException {
    Path providerStoragePath = Paths.get(PROVIDER_RESOURCE_DIR,
            StorageUtil.generateProviderStorageDirectory(archive.getEntity(), version));

    if (!providerStoragePath.toFile().exists()) {
      try {
        Files.createDirectories(providerStoragePath);
      } catch (IOException e) {
        throw new StorageException(archive.getEntity().getId(), e);
      }
    }
    try (Stream<Path> stream = Files.walk(archive.getCompressedFile().getParentFile().toPath())) {
      stream.filter(
                      path -> path.toFile().isFile()
                              && !path.toString().endsWith(".tmp"))
              .forEach(pathToFile -> {
                try {
                  Files.copy(
                          pathToFile,
                          Paths.get(
                                  providerStoragePath.toString(),
                                  pathToFile.getFileName().toString()
                          ),
                          StandardCopyOption.REPLACE_EXISTING
                  );
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              });
    } catch (Exception ex) {
      throw new StorageException(archive.getEntity().getId(), ex);
    }
  }
}
