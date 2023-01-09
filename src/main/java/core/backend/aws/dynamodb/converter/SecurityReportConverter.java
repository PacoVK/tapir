package core.backend.aws.dynamodb.converter;

import extensions.security.report.TfSecReport;
import io.vertx.core.json.JsonObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.ListAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.MapAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.string.StringStringConverter;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class SecurityReportConverter implements AttributeConverter<Map<String, List>> {

  private final MapAttributeConverter<Map<String, List>> mapAttributeConverter =
          MapAttributeConverter.builder(EnhancedType.mapOf(String.class, List.class))
                  .mapConstructor(HashMap::new)
                  .keyConverter(StringStringConverter.create())
                  .valueConverter(new TfSecResultListConverter())
                  .build();

  @Override
  public AttributeValue transformFrom(Map<String, List> stringTfSecReportMap) {
    return mapAttributeConverter.transformFrom(stringTfSecReportMap);
  }

  @Override
  public Map<String, List> transformTo(AttributeValue attributeValue) {
    return mapAttributeConverter.transformTo(attributeValue);
  }

  @Override
  public EnhancedType<Map<String, List>> type() {
    return mapAttributeConverter.type();
  }

  @Override
  public AttributeValueType attributeValueType() {
    return mapAttributeConverter.attributeValueType();
  }

  static class TfSecResultListConverter implements AttributeConverter<List> {

    private final ListAttributeConverter<Collection<TfSecReport.TfSecResult>>
            listAttributeConverter = ListAttributeConverter
            .builder(EnhancedType.collectionOf(TfSecReport.TfSecResult.class))
            .collectionConstructor(LinkedList::new)
            .elementConverter(new TfSecResultConverter()).build();

    @Override
    public AttributeValue transformFrom(List list) {
      return listAttributeConverter.transformFrom(list);
    }

    @Override
    public List transformTo(AttributeValue attributeValue) {
      return (List) listAttributeConverter.transformTo(attributeValue);
    }

    @Override
    public EnhancedType<List> type() {
      return EnhancedType.of(List.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
      return listAttributeConverter.attributeValueType();
    }
  }

  static class TfSecResultConverter implements AttributeConverter<TfSecReport.TfSecResult> {
    @Override
    public AttributeValue transformFrom(TfSecReport.TfSecResult securityReportResults) {
      return AttributeValue.fromS(JsonObject.mapFrom(securityReportResults).encode());
    }

    @Override
    public TfSecReport.TfSecResult transformTo(AttributeValue attributeValue) {
      return new JsonObject(attributeValue.s()).mapTo(TfSecReport.TfSecResult.class);
    }

    @Override
    public EnhancedType<TfSecReport.TfSecResult> type() {
      return EnhancedType.of(TfSecReport.TfSecResult.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
      return AttributeValueType.S;
    }
  }
}
