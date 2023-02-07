package core.upload.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import core.terraform.ProviderPlatform;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class FileServiceTest {

  FileService service = new FileService();

  @Test
  void createTempModuleArchiveFile() throws IOException {
    File tempModuleArchiveFile = service.createTempArchiveFile("bar-0.0.0");
    assertTrue(tempModuleArchiveFile.isFile());
    assertTrue(tempModuleArchiveFile.exists());
    assertTrue(tempModuleArchiveFile.getName().startsWith("bar-0.0.0"));
  }

  @Test
  void unpackArchiveAndDeleteAllFiles() throws URISyntaxException, IOException {
    URL resource = getClass().getClassLoader().getResource("test-module.zip");
    Path testDirectory = Files.createTempDirectory(null);
    assert resource != null;
    File archive=  new File(resource.toURI());
    service.unpackArchive(archive, testDirectory);
    File[] files = testDirectory.toFile().listFiles();
    assert files != null;
    assertEquals(files.length, 1);
    assertTrue(files[0].getName().endsWith("main.tf"));
    File testDirectoryFile = testDirectory.toFile();
    assertTrue(testDirectoryFile.isDirectory());
    service.deleteAllFilesInDirectory(testDirectoryFile);
    assertFalse(testDirectoryFile.isDirectory());
  }

  @Test
  void getProviderPlatformsFromShaSumsFileTest() throws URISyntaxException {
    URL resource = getClass().getClassLoader().getResource("unpackedProviderStub");
    assert resource != null;
    List<ProviderPlatform> platforms = service.getProviderPlatformsFromShaSumsFile(
            new File(resource.toURI())
    );
    assertEquals(platforms.size(), 14);
    ProviderPlatform platform = platforms.get(0);
    assertEquals(platform.getFileName(), "terraform_1.3.7_darwin_amd64.zip");
    assertEquals(platform.getOs(), "darwin");
    assertEquals(platform.getArch(), "amd64");
    assertEquals(platform.getShasum(), "b00465acc7bdef57ba468b84b9162786e472dc97ad036a9e3526dde510563e2d");
  }
}