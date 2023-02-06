package api.dto.serializer;

import api.dto.GpgPublicKey;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.List;

public class GpgPublicKeySerializer extends StdSerializer<List<GpgPublicKey>> {

  public GpgPublicKeySerializer() {
    this(null);
  }

  protected GpgPublicKeySerializer(Class<List<GpgPublicKey>> t) {
    super(t);
  }

  @Override
  public void serialize(
          List<GpgPublicKey> gpgPublicKeys,
          JsonGenerator jsonGenerator,
          SerializerProvider serializerProvider
  ) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeFieldName("gpg_public_keys");
    jsonGenerator.writeObject(gpgPublicKeys);
    jsonGenerator.writeEndObject();
  }
}
