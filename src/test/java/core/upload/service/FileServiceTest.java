package core.upload.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
    URL resource = getClass().getClassLoader().getResource("test-archive.zip");
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
}