package extensions.security.util;

import extensions.security.report.TfSecReport;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RegisterForReflection
public class TfSecReportUtil {

  public static HashMap<String, List<TfSecReport.TfSecResult>> sanitizeAndGroupAndSortFindings(
          TfSecReport report, Path tmpWorkingDirectory) {
    return report
            .getResults()
            .stream()
            .sorted(Comparator.comparing(TfSecReport.TfSecResult::getSeverity))
            .collect(Collectors.groupingBy(
                    repo -> {
                      String[] files = repo.getLocation().getFilename().split(
                              String.format("%s/", tmpWorkingDirectory.toString())
                      );
                      return files.length > 1 ? files[1] : files[0];
                    },
                    HashMap::new,
                    Collectors.toList()
            ));
  }
}
