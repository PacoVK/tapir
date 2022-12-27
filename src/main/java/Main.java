import core.service.backend.SearchService;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import javax.enterprise.inject.Instance;

@QuarkusMain
public class Main {
  public static void main(String[] args) {
    Quarkus.run(TerraformRegistry.class, args);
  }

  private static class TerraformRegistry implements QuarkusApplication {

    SearchService searchService;

    public TerraformRegistry(Instance<SearchService> searchService) {
      this.searchService = searchService.get();
    }

    @Override
    public int run(String... args) throws Exception {
      searchService.bootstrap();
      Quarkus.waitForExit();
      return 0;
    }
  }
}
