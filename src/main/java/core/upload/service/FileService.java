package core.upload.service;

import core.terraform.ProviderPlatform;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@ApplicationScoped
public class FileService {

  static final Logger LOGGER = Logger.getLogger(FileService.class.getName());

  byte[] buffer = new byte[1024];

  public File createTempArchiveFile(String fileName) throws IOException {
    Path tmp = Files.createTempDirectory(null);
    return File.createTempFile(
            fileName,
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
                && !entry.getName().contains("example")
                && !entry.getName().startsWith(".")
                && !entry.getName().contains("MACOS")
                && (entry.getName().endsWith(".tf")
                || entry.getName().endsWith(".zip")
                || entry.getName().endsWith(".exe")
                || entry.getName().endsWith("SHA256SUMS.sig")
                || entry.getName().endsWith("SHA256SUMS")
                || entry.getName().startsWith("terraform-provider-"))) {
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

  public List<ProviderPlatform> getProviderPlatformsFromShaSumsFile(File directory) {
    File sha256SumsFile = Arrays.stream(
            Objects.requireNonNull(directory.listFiles()))
            .filter(file -> file.getName().endsWith("SHA256SUMS"))
            .findFirst()
            .orElseThrow();
    try (BufferedReader reader = new BufferedReader(new FileReader(sha256SumsFile))) {
      return reader.lines().map(line -> {
        String[] split = line.split("\\s+");
        String shaSum = split[0];
        String fileName = split[1];
        String[] fileNameSplit = fileName.replace(".zip", "").split("_");
        String os = fileNameSplit[fileNameSplit.length - 2];
        String arch = fileNameSplit[fileNameSplit.length - 1];
        return new ProviderPlatform(os, arch, shaSum, fileName);
      }).toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private File createOrGetFile(String fileName, Path targetDir) throws IOException {
    File tmpFile;
    if (!Files.exists(targetDir.resolve(fileName))) {
      Path path = Paths.get(fileName);
      if(path.getParent() != null) {
        Files.createDirectories(targetDir.resolve(path.getParent()));
      }
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
