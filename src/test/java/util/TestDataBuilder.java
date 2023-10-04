package util;

import core.terraform.Module;
import extensions.core.Report;
import extensions.security.report.TrivyReport;
import io.vertx.core.json.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestDataBuilder {

  public static TrivyReport getSecurityReportStub(){
    try (Reader reader = new InputStreamReader(Objects.requireNonNull(TestDataBuilder.class
            .getResourceAsStream("/trivyReport.json")))) {
      String report = new BufferedReader(reader).lines().collect(Collectors.joining());
      return new JsonObject(report).mapTo(TrivyReport.class);
    }catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Report getReportStub(Module module){
    Report report = new Report(
            module.getNamespace(),
            module.getName(),
            module.getProvider(),
            module.getCurrentVersion()
    );
    report.setSecurityReport(Collections.EMPTY_MAP);
    return report;
  }
}
