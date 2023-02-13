package util;

import core.config.SigningKeys;
import java.util.Set;

public class SigningKeysMockImpl implements SigningKeys {

  Set<SigningKey> keys;

  public SigningKeysMockImpl(Set<SigningKey> keys) {
    this.keys = keys;
  }

  @Override
  public Set<SigningKey> keys() {
    return keys;
  }

  public static class SigningKeyMockImpl implements SigningKey {

    String id;
    String asciiArmor;

    public SigningKeyMockImpl(String id, String asciiArmor) {
      this.id = id;
      this.asciiArmor = asciiArmor;
    }

    @Override
    public String id() {
      return id;
    }

    @Override
    public String asciiArmor() {
      return asciiArmor;
    }
  }
}
