package util;

import extensions.core.SastReport;

import java.util.List;
import java.util.Map;

public class TestDataBuilder {

  static final SastReport sastReportStub = new SastReport(
          "mycorp",
          "example",
          "provider",
          "0.0.0",
          null
          /*
          Map.of(
                  "SchemaVersion",2,
                  "ArtifactName", ".",
                  "ArtifactType", "filesystem",
                  "Metadata", Map.of("ImageConfig",
                          Map.of("architecture", "",
                                "created", "0001-01-01T00:00:00Z",
                                "os", "",
                                "rootfs", Map.of("type", "", "diff_ids", ""),
                                "config", Map.of())),
                  "Result", List.of(
                          Map.of(
                                  "Target", "foo.tf",
                                  "Class", "config",
                                  "Type", "terraform",
                                  "MisconfSummary", Map.of(
                                          "Successes", 0,
                                          "Failures", 1,
                                          "Exceptions", 0
                                  ),
                                  "Misconfigurations", List.of(
                                          Map.of()
                                  )
                          )
                  )
          )
           */
  );

  public static SastReport getSastReportStub() {
    return sastReportStub;
  }
}
