package core.storage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

/**
 * Health check for the configured storage backend.
 * This class serves as both a readiness and liveness probe, verifying that the storage backend is accessible and functional.
 */
@Readiness
@Liveness
@ApplicationScoped
public class StorageHealthCheck implements HealthCheck {

  private static final Logger LOGGER = Logger.getLogger(StorageHealthCheck.class.getName());

  @Inject
  Instance<StorageRepository> storage;

  @Override
  public HealthCheckResponse call() {
    HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("storage");

    if (!storage.isResolvable()) {
      LOGGER.log(Level.SEVERE, "No storage repository configured");
      return responseBuilder
          .down()
          .build();
    }

    StorageRepository repo = storage.get();

    String backendType = ConfigProvider.getConfig()
        .getConfigValue("registry.storage.backend").getValue();

    try {
      // Call the checkHealth() method implemented by the storage repository
      repo.checkHealth();
      return responseBuilder
          .up()
          .withData("backend", backendType)
          .build();
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Storage health check failed for " + backendType, e);
      return responseBuilder
          .down()
          .withData("backend", backendType)
          .build();
    }
  }
}
