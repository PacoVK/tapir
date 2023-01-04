import core.service.backend.SearchService;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import java.util.logging.Logger;
import javax.enterprise.inject.Instance;
import org.eclipse.microprofile.config.ConfigProvider;

@QuarkusMain
public class Main {
  public static void main(String[] args) {
    Quarkus.run(TerraformRegistry.class, args);
  }

  private static class TerraformRegistry implements QuarkusApplication {

    static final Logger LOGGER = Logger.getLogger(TerraformRegistry.class.getName());

    SearchService searchService;

    public TerraformRegistry(Instance<SearchService> searchService) {
      this.searchService = searchService.get();
    }

    @Override
    public int run(String... args) throws Exception {
      LOGGER.info(
              String.format("Start to bootstrap registry database [%s]",
                      ConfigProvider.getConfig()
                              .getConfigValue("registry.search.backend").getValue()
              )
      );
      searchService.bootstrap();
      Quarkus.waitForExit();
      return 0;
    }
  }
}
