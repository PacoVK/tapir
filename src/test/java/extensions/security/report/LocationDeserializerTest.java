package extensions.security.report;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LocationDeserializerTest {

  LocationDeserializer deserializer = new LocationDeserializer();

  @Test
  void deserialize() {
    String locationJson =
            """
              {
                "filename": "/foo/bar/github.tf",
                "start_line": 32,
                "end_line": 39
              }
            """;

    JsonFactory factory = new JsonFactory(new ObjectMapper().getFactory().getCodec());
    try(JsonParser parser  = factory.createParser(locationJson)) {
      TfSecReport.TfSecResult.Location location = deserializer.deserialize(parser, null);
      assertEquals(location.getFilename(), "github.tf");
      assertEquals(location.getStart_line(), 32);
      assertEquals(location.getEnd_line(), 39);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}