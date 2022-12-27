package extensions.security.report;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

public class LocationDeserializer extends StdDeserializer<TfSecReport.TfSecResult.Location> {

  public LocationDeserializer() {
    this(null);
  }

  protected LocationDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public TfSecReport.TfSecResult.Location deserialize(
          JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode node = jsonParser.getCodec().readTree(jsonParser);
    String filename = node.get("filename").asText();
    Integer startLine = (Integer) node.get("start_line").numberValue();
    Integer endLine = (Integer) node.get("end_line").numberValue();

    String targetFileName = filename.substring(filename.lastIndexOf("/") + 1);

    return new TfSecReport.TfSecResult.Location(targetFileName, startLine, endLine);
  }
}
