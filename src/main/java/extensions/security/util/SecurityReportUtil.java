package extensions.security.util;

import extensions.security.report.SecurityFinding;
import extensions.security.report.TrivyReport;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RegisterForReflection
public class SecurityReportUtil {

  public static HashMap<String, List<SecurityFinding>> transformTrivyReportAndGroupAndSortFindings(
      TrivyReport report) {
    return report
        .getResults()
        .stream().map(result -> {
          if (result.getMisconfigurations() != null) {
            return result.getMisconfigurations().stream().map(misconfigurations ->
                    new SecurityFinding(
                        misconfigurations.getId(),
                        misconfigurations.getId(),
                        misconfigurations.getMessage(),
                        misconfigurations.getCauseMetadata().getProvider(),
                        misconfigurations.getCauseMetadata().getService(),
                        misconfigurations.getDescription(),
                        misconfigurations.getResolution(),
                        misconfigurations.getReferences(),
                        misconfigurations.getTitle(),
                        misconfigurations.getSeverity(),
                        false,
                        0,
                        misconfigurations.getCauseMetadata().getResource(),
                        new SecurityFinding.Location(
                            result.getTarget(),
                            misconfigurations.getCauseMetadata().getStartLine(),
                            misconfigurations.getCauseMetadata().getEndLine()
                        )
                    )
                )
                .sorted(Comparator.comparing(SecurityFinding::getSeverity))
                .collect(
                    Collectors.groupingBy(
                        misconfigurations -> result.getTarget(),
                        HashMap::new,
                        Collectors.toList()
                    ));
          }
          return new HashMap<String, List<SecurityFinding>>();
        }).flatMap(map -> map.entrySet().stream()).collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (a, b) -> {
              a.addAll(b);
              return a;
            },
            HashMap::new
        ));
  }
}
