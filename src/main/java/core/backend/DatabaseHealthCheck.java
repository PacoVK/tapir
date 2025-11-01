package core.backend;

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
 * Health check for the configured database backend
 * This readiness probe verifies that the backend is accessible and responsive.
 */
@Readiness
@Liveness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {

  private static final Logger LOGGER = Logger.getLogger(DatabaseHealthCheck.class.getName());

  @Inject
  Instance<IRepository> repository;

  @Override
  public HealthCheckResponse call() {
    HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("database");

    if (!repository.isResolvable()) {
      LOGGER.log(Level.SEVERE, "No database repository configured");
      return responseBuilder
          .down()
          .build();
    }

    IRepository repo = repository.get();

    String backendType = ConfigProvider.getConfig()
        .getConfigValue("registry.search.backend").getValue();

    try {
      repo.checkHealth();
      return responseBuilder
          .up()
          .withData("backend", backendType)
          .build();
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Database health check failed for " + backendType, e);
      return responseBuilder
          .down()
          .withData("backend", backendType)
          .build();
    }
  }
}
