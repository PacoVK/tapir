package core.config;

import io.smallrye.config.ConfigMapping;
import java.util.Set;

@ConfigMapping(prefix = "registry.gpg")
public interface SigningKeys {
  Set<SigningKey> keys();

  interface SigningKey {
    String id();

    String asciiAmor();
  }
}
