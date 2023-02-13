package api.dto.serializer;

import api.dto.GpgPublicKey;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Base64;
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
    Base64.Decoder decoder = Base64.getDecoder();
    List<GpgPublicKey> publicKeyList = gpgPublicKeys.stream().peek(key -> {
      String asciiArmoredDecoded = new String(decoder.decode(key.getAsciiAmor()));
      key.setAsciiAmor(asciiArmoredDecoded);
    }).toList();
    jsonGenerator.writeStartObject();
    jsonGenerator.writeFieldName("gpg_public_keys");
    jsonGenerator.writeObject(publicKeyList);
    jsonGenerator.writeEndObject();
  }
}
