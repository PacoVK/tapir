package extensions.security.util;

import extensions.security.report.TfSecReport;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RegisterForReflection
public class TfSecReportUtil {

  public static HashMap<String, List<TfSecReport.TfSecResult>> groupAndSortFindingsBySeverity(
          TfSecReport report) {
    return report
            .getResults()
            .stream()
            .sorted(Comparator.comparing(TfSecReport.TfSecResult::getSeverity))
            .collect(Collectors.groupingBy(
                    repo -> repo.getLocation().getFilename(),
                    HashMap::new,
                    Collectors.toList()
            ));
  }
}
