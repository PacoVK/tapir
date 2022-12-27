package extensions.security.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import extensions.security.report.Severity;
import extensions.security.report.TfSecReport;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import util.TestDataBuilder;

class TfSecReportUtilTest {

  @Test
  void groupAndSortFindingsBySeverity() {
    TfSecReport tfSecReportStub = TestDataBuilder.getTfSecReportStub();
    assert tfSecReportStub != null;
    HashMap<String, List<TfSecReport.TfSecResult>> findings = TfSecReportUtil
            .sanitizeAndGroupAndSortFindings(tfSecReportStub, Paths.get("/foo/bar"));
    assertEquals(findings.size(), 2);
    assertEquals(findings.get("github.tf").size(), 3);
    assertEquals(findings.get("github.tf").get(0).getSeverity(), Severity.CRITICAL);
    assertEquals(findings.get("github.tf").get(1).getSeverity(), Severity.LOW);
    assertEquals(findings.get("github.tf").get(2).getSeverity(), Severity.UNKNOWN);
    assertEquals(findings.get("kms.tf").size(), 2);
  }
}