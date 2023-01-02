package util;

import core.terraform.Module;
import extensions.core.SastReport;
import extensions.security.report.TfSecReport;
import io.vertx.core.json.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestDataBuilder {

  public static TfSecReport getTfSecReportStub(){
    try (Reader reader = new InputStreamReader(Objects.requireNonNull(TestDataBuilder.class
            .getResourceAsStream("/tfsecReport.json")))) {
      String report = new BufferedReader(reader).lines().collect(Collectors.joining());
      return new JsonObject(report).mapTo(TfSecReport.class);
    }catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  public static SastReport getSastReportStub(Module module){
    return new SastReport(
            module.getNamespace(),
            module.getName(),
            module.getProvider(),
            module.getCurrentVersion(),
            Collections.EMPTY_MAP
    );
  }
}
