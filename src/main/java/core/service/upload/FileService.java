package core.service.upload;

import core.terraform.Module;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FileService {

  static final Logger LOGGER = Logger.getLogger(FileService.class.getName());

  byte[] buffer = new byte[1024];

  public File createTempModuleArchiveFile(Module module) throws IOException {
    Path tmp = Files.createTempDirectory(null);
    return File.createTempFile(
            String.format("%s-%s", module.getName(), module.getCurrentVersion()),
            null,
            tmp.toFile()
    );
  }

  public void flushArchiveToFile(File archive, File targetFile) {
    try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
        InputStream inputStream = new FileInputStream(archive)
    ) {
      fileOutputStream.write(inputStream.readAllBytes());
      fileOutputStream.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void unpackArchive(File archive, Path targetDir) {
    try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(archive))) {
      ZipEntry entry = zipInputStream.getNextEntry();
      while (entry != null) {
        if (!entry.isDirectory()
                && !entry.getName().startsWith("example")
                && entry.getName().endsWith(".tf")) {
          File tempFile = createOrGetFile(entry.getName(), targetDir);
          FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
          int len;
          while ((len = zipInputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, len);
          }
          fileOutputStream.flush();
          fileOutputStream.close();
        }
        entry = zipInputStream.getNextEntry();
      }
      zipInputStream.closeEntry();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteAllFilesInDirectory(File directory) throws IOException {
    if (directory.isDirectory()) {
      File[] files = directory.listFiles();

      if (files != null) {
        for (File file : files) {
          deleteAllFilesInDirectory(file);
        }
      }
    }

    if (directory.delete()) {
      LOGGER.fine(String.format("Deleted file %s", directory.toPath()));
      Files.deleteIfExists(directory.toPath());
    }
  }

  private File createOrGetFile(String fileName, Path targetDir) throws IOException {
    File tmpFile;
    if (!Files.exists(targetDir.resolve(fileName))) {
      tmpFile = Files.createFile(targetDir.resolve(fileName)).toFile();
    } else {
      tmpFile = targetDir.resolve(fileName).toFile();
    }
    if (!tmpFile.getCanonicalPath().startsWith(targetDir.toRealPath() + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + fileName);
    }
    return tmpFile;
  }
}
