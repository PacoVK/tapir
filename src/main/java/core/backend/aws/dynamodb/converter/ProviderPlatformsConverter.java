package core.backend.aws.dynamodb.converter;

import core.terraform.ArtifactVersion;
import core.terraform.ProviderPlatform;
import io.vertx.core.json.JsonObject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.StringConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.ListAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.MapAttributeConverter;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class ProviderPlatformsConverter
        implements AttributeConverter<Map<ArtifactVersion, Collection>> {

  private final MapAttributeConverter<Map<ArtifactVersion, Collection>> mapAttributeConverter =
          MapAttributeConverter
                  .builder(EnhancedType.mapOf(ArtifactVersion.class, Collection.class))
                  .mapConstructor(TreeMap<ArtifactVersion, List<ProviderPlatform>>::new)
                  .keyConverter(new VersionStringConverter())
                  .valueConverter(new ProviderPlatformListConverter()).build();

  @Override
  public AttributeValue transformFrom(Map<ArtifactVersion, Collection> providerPlatforms) {
    return mapAttributeConverter.transformFrom(providerPlatforms);
  }

  @Override
  public Map<ArtifactVersion, Collection> transformTo(AttributeValue attributeValue) {
    return mapAttributeConverter.transformTo(attributeValue);
  }

  @Override
  public EnhancedType<Map<ArtifactVersion, Collection>> type() {
    return mapAttributeConverter.type();
  }

  @Override
  public AttributeValueType attributeValueType() {
    return mapAttributeConverter.attributeValueType();
  }

  static class VersionStringConverter implements StringConverter<ArtifactVersion> {
    @Override
    public String toString(ArtifactVersion object) {
      return object.getVersion();
    }

    @Override
    public ArtifactVersion fromString(String s) {
      return new ArtifactVersion(s);
    }

    @Override
    public EnhancedType<ArtifactVersion> type() {
      return EnhancedType.of(ArtifactVersion.class);
    }
  }

  static class ProviderPlatformListConverter implements AttributeConverter<Collection> {

    private final ListAttributeConverter<Collection<ProviderPlatform>> listAttributeConverter =
            ListAttributeConverter
                    .builder(EnhancedType.collectionOf(ProviderPlatform.class))
                    .collectionConstructor(LinkedList::new)
                    .elementConverter(new ProviderPlatformConverter()).build();

    @Override
    public AttributeValue transformFrom(Collection collection) {
      return listAttributeConverter.transformFrom(collection);
    }

    @Override
    public Collection<ProviderPlatform> transformTo(AttributeValue attributeValue) {
      return listAttributeConverter.transformTo(attributeValue);
    }

    @Override
    public EnhancedType<Collection> type() {
      return EnhancedType.of(Collection.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
      return listAttributeConverter.attributeValueType();
    }
  }

  static class ProviderPlatformConverter implements AttributeConverter<ProviderPlatform> {
    @Override
    public AttributeValue transformFrom(ProviderPlatform providerPlatform) {
      return AttributeValue.fromS(JsonObject.mapFrom(providerPlatform).encode());
    }

    @Override
    public ProviderPlatform transformTo(AttributeValue attributeValue) {
      return new JsonObject(attributeValue.s()).mapTo(ProviderPlatform.class);
    }

    @Override
    public EnhancedType<ProviderPlatform> type() {
      return EnhancedType.of(ProviderPlatform.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
      return AttributeValueType.S;
    }
  }
}
