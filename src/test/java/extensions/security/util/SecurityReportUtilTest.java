package extensions.security.util;

import extensions.security.report.SecurityFinding;
import extensions.security.report.Severity;
import extensions.security.report.TrivyReport;
import org.junit.jupiter.api.Test;
import util.TestDataBuilder;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityReportUtilTest {

  @Test
  void groupAndSortFindingsBySeverity() {
    TrivyReport trivyReportStub = TestDataBuilder.getSecurityReportStub();
    assert trivyReportStub != null;
    HashMap<String, List<SecurityFinding>> findings = SecurityReportUtil
            .transformTrivyReportAndGroupAndSortFindings(trivyReportStub);
    assertEquals(findings.size(), 3);
    assertEquals(findings.get("github.tf").size(), 3);
    assertEquals(findings.get("github.tf").get(0).getSeverity(), Severity.CRITICAL);
    assertEquals(findings.get("github.tf").get(1).getSeverity(), Severity.LOW);
    assertEquals(findings.get("github.tf").get(2).getSeverity(), Severity.LOW);
    assertEquals(findings.get("kms.tf").size(), 2);
  }
}