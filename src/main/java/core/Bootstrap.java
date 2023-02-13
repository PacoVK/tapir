package core;

import core.backend.SearchService;
import core.config.SigningKeys;
import core.exceptions.InvalidConfigurationException;
import java.util.Base64;
import java.util.logging.Logger;
import javax.enterprise.inject.Instance;
import javax.inject.Singleton;
import org.eclipse.microprofile.config.ConfigProvider;

@Singleton
public class Bootstrap {

  static final Logger LOGGER = Logger.getLogger(Bootstrap.class.getName());

  SearchService searchService;
  SigningKeys signingKeys;

  public Bootstrap(Instance<SearchService> searchServiceInstance, SigningKeys signingKeys) {
    this.searchService = searchServiceInstance.get();
    this.signingKeys = signingKeys;
  }

  public void bootstrap() throws Exception {
    validateGpgKeys();
    LOGGER.info(
            String.format("Start to bootstrap registry database [%s]",
                    ConfigProvider.getConfig()
                            .getConfigValue("registry.search.backend").getValue()
            )
    );
    searchService.bootstrap();
  }

  void validateGpgKeys() throws InvalidConfigurationException {
    LOGGER.info("Validate GPG key configuration provided");
    try {
      signingKeys.keys().forEach(key -> Base64.getDecoder().decode(key.asciiArmor()));
    } catch (IllegalArgumentException ex) {
      throw new InvalidConfigurationException("registry.gpg.keys", ex);
    }

  }
}
