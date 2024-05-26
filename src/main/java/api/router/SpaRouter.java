package api.router;

import io.vertx.ext.web.Router;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/*
 * This class is responsible for routing requests to the SPA. It's currently a workaround for the issue https://github.com/quarkiverse/quarkus-quinoa/issues/666
 * It may be removed once Quinoa features https://github.com/quarkiverse/quarkus-quinoa/issues/302
 * Tapir cannot use a specific path for all API requests, as it would conflict with the Terraform registry protocol spec.
 * Implementation based on https://docs.quarkiverse.io/quarkus-quinoa/dev/advanced-guides.html
 */
@ApplicationScoped
public class SpaRouter {

  private static final String[] PATH_PREFIXES = {
      "/q/",
      "/terraform/",
      "/static/",
      "/tapir/",
      "/search/",
      "/reports/",
      "/management/deploykey/",
      "/.well-known/"
  };
  private static final Predicate<String> FILE_NAME_PREDICATE =
      Pattern.compile(".+\\.[a-zA-Z0-9]+$").asMatchPredicate();

  public void init(@Observes Router router) {
    router.get("/*").handler(rc -> {
      final String path = rc.normalizedPath();
      if (!path.equals("/")
          && Stream.of(PATH_PREFIXES).noneMatch(path::startsWith)
          && !FILE_NAME_PREDICATE.test(path)) {
        rc.reroute("/");
      } else {
        rc.next();
      }
    });
  }
}
