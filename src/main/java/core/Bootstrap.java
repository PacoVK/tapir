package core;

import core.backend.TapirRepository;
import core.config.SigningKeys;
import core.exceptions.InvalidConfigurationException;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Singleton;
import java.util.Base64;
import java.util.logging.Logger;
import org.eclipse.microprofile.config.ConfigProvider;

@Singleton
public class Bootstrap {

  static final Logger LOGGER = Logger.getLogger(Bootstrap.class.getName());

  TapirRepository tapirRepository;
  SigningKeys signingKeys;

  public Bootstrap(Instance<TapirRepository> searchServiceInstance, SigningKeys signingKeys) {
    this.tapirRepository = searchServiceInstance.get();
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
    tapirRepository.bootstrap();
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
