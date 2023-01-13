package core.backend.aws.dynamodb.converter;

import extensions.docs.report.TerraformDocumentation;
import io.vertx.core.json.JsonObject;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class TerraformDocumentationConverter implements AttributeConverter<TerraformDocumentation> {
  @Override
  public AttributeValue transformFrom(TerraformDocumentation documentation) {
    return AttributeValue.fromS(JsonObject.mapFrom(documentation).encode());
  }

  @Override
  public TerraformDocumentation transformTo(AttributeValue attributeValue) {
    return new JsonObject(attributeValue.s()).mapTo(TerraformDocumentation.class);
  }

  @Override
  public EnhancedType<TerraformDocumentation> type() {
    return EnhancedType.of(TerraformDocumentation.class);
  }

  @Override
  public AttributeValueType attributeValueType() {
    return AttributeValueType.S;
  }
}
