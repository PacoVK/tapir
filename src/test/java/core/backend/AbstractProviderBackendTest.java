package core.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import api.dto.PaginationDto;
import core.terraform.ArtifactVersion;
import core.terraform.Provider;
import core.terraform.ProviderPlatform;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractProviderBackendTest {

  TapirRepository repository;

  public AbstractProviderBackendTest(TapirRepository repository) {
    this.repository = repository;
  }

  @BeforeEach
  void setUp() throws Exception {
    repository.bootstrap();
  }

  @Test
  void ingestProviderData() throws Exception {
    Provider provider = new Provider("ape", "bar");
    TreeMap<ArtifactVersion, List<ProviderPlatform>> platform = new TreeMap<>(
            Map.of(new ArtifactVersion("0.0.0"), List.of(new ProviderPlatform(
                    "os",
                    "arch"
            )))
    );
    provider.setVersions(platform);
    repository.ingestProviderData(provider);
    Provider ingestedProvider = repository.getProvider(provider.getId());
    assertEquals(provider.getId(), ingestedProvider.getId());
    assertEquals(provider.getVersions().firstKey().getVersion(),
            ingestedProvider.getVersions().firstKey().getVersion());
  }

  @Test
  void findProviders() throws Exception {
    Provider provider1 = new Provider("foo", "bar");
    Provider provider2 = new Provider("fred", "flintstone");
    TreeMap<ArtifactVersion, List<ProviderPlatform>> platform = new TreeMap<>(
            Map.of(new ArtifactVersion("0.0.0"), List.of(new ProviderPlatform(
                    "os",
                    "arch"
            )))
    );
    provider1.setVersions(platform);
    provider2.setVersions(platform);
    repository.ingestProviderData(provider1);
    repository.ingestProviderData(provider2);
    PaginationDto noIdentifier = repository.findProviders("", 5, "fred");
    PaginationDto withIdentifier = repository.findProviders(provider1.getId(), 5, "f");
    PaginationDto all = repository.findProviders("", 5, "");
    assertEquals(noIdentifier.getEntities().size(), 1);
    assertEquals(withIdentifier.getEntities().size(), 1);
    assertEquals(all.getEntities().size(), 2);
  }
}
