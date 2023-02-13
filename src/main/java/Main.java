import core.Bootstrap;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main {
  public static void main(String[] args) {
    Quarkus.run(TerraformRegistry.class, args);
  }

  private static class TerraformRegistry implements QuarkusApplication {

    Bootstrap bootstrapService;

    public TerraformRegistry(Bootstrap bootstrapService) {
      this.bootstrapService = bootstrapService;
    }

    @Override
    public int run(String... args) throws Exception {
      bootstrapService.bootstrap();
      Quarkus.waitForExit();
      return 0;
    }
  }
}
