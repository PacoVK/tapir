package core.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class VersionServiceTest {

  @Test
  void isValidModuleVersion() {
    assertTrue(VersionService.isValidModuleVersion("1.0.0"));
    assertTrue(VersionService.isValidModuleVersion("v1.0.0"));
    assertTrue(VersionService.isValidModuleVersion("2.1.3-alpha"));
    assertTrue(VersionService.isValidModuleVersion("0.0.1-beta+exp.sha.5114f85"));
    assertTrue(VersionService.isValidModuleVersion("1.0.0+20130313144700"));
    assertFalse(VersionService.isValidModuleVersion("1.0"));
    assertFalse(VersionService.isValidModuleVersion("1.Foo.0"));
  }

  @Test
  void isValidProviderVersion() {
    assertTrue(VersionService.isValidProviderVersion("v1.0.0"));
    assertTrue(VersionService.isValidProviderVersion("v2.1.3-alpha"));
    assertTrue(VersionService.isValidProviderVersion("v0.0.1-beta+exp.sha.5114f85"));
    assertFalse(VersionService.isValidProviderVersion("1.0.0"));
    assertFalse(VersionService.isValidProviderVersion("1.0.0+20130313144700"));
    assertFalse(VersionService.isValidProviderVersion("v1.0"));
    assertFalse(VersionService.isValidProviderVersion("v1.Foo.0"));
  }
}