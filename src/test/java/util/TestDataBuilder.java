package util;

import core.terraform.Module;
import extensions.core.SastReport;
import extensions.security.report.TrivyReport;
import io.vertx.core.json.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestDataBuilder {

  public static SastReport getTrivyReportStub(Module module){
    try (Reader reader = new InputStreamReader(Objects.requireNonNull(TestDataBuilder.class
            .getResourceAsStream("/trivyReport.json")))) {
      String report = new BufferedReader(reader).lines().collect(Collectors.joining());
      TrivyReport trivyReport = new JsonObject(report).mapTo(TrivyReport.class);
      return new SastReport(
              module.getNamespace(),
              module.getName(),
              module.getProvider(),
              module.getCurrentVersion(),
              trivyReport
      );
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
