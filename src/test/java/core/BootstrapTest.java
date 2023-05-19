package core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import core.backend.SearchService;
import core.exceptions.InvalidConfigurationException;
import java.util.Set;
import jakarta.enterprise.inject.Instance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import util.SigningKeysMockImpl;

class BootstrapTest {

  Instance<SearchService> searchService = Mockito.mock(Instance.class);

  @BeforeEach
  void setUp() {
    Mockito.when(searchService.get()).thenReturn(null);
  }

  @Test
  void validateGpgKeys() {
    SigningKeysMockImpl validSigningKeys = new SigningKeysMockImpl(
            Set.of(new SigningKeysMockImpl.SigningKeyMockImpl("fakeId", "ZmFrZWtleQo="))
    );
    Bootstrap bootstrap = new Bootstrap(searchService, validSigningKeys);
    assertDoesNotThrow(bootstrap::validateGpgKeys);
  }

  @Test
  void validateGpgKeysThrowsError() {
    Mockito.when(searchService.get()).thenReturn(null);
    SigningKeysMockImpl invalidSigningKeys = new SigningKeysMockImpl(
            Set.of(new SigningKeysMockImpl.SigningKeyMockImpl("fakeId", "jkdsfhKJH"))
    );
    Bootstrap bootstrap = new Bootstrap(searchService, invalidSigningKeys);
    Exception exception = assertThrows(InvalidConfigurationException.class, bootstrap::validateGpgKeys);
    String expectedMessage = "Configuration for registry.gpg.keys is invalid";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }
}