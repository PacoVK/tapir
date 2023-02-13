package core.backend.aws.dynamodb.converter;

import core.terraform.ArtifactVersion;
import java.util.Collection;
import java.util.TreeSet;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.ListAttributeConverter;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;


public class ArtifactVersionsConverter implements AttributeConverter<Collection<ArtifactVersion>> {

  private final ListAttributeConverter<Collection<ArtifactVersion>> listAttributeConverter =
          ListAttributeConverter
          .builder(EnhancedType.collectionOf(ArtifactVersion.class))
          .collectionConstructor(TreeSet::new)
          .elementConverter(new ArtifactVersionConverter()).build();

  @Override
  public AttributeValue transformFrom(Collection<ArtifactVersion> moduleVersions) {
    return listAttributeConverter.transformFrom(moduleVersions);
  }

  @Override
  public Collection<ArtifactVersion> transformTo(AttributeValue attributeValue) {
    return listAttributeConverter.transformTo(attributeValue);
  }

  @Override
  public EnhancedType<Collection<ArtifactVersion>> type() {
    return listAttributeConverter.type();
  }

  @Override
  public AttributeValueType attributeValueType() {
    return listAttributeConverter.attributeValueType();
  }

  static class ArtifactVersionConverter implements AttributeConverter<ArtifactVersion> {

    @Override
    public AttributeValue transformFrom(ArtifactVersion moduleVersion) {
      return AttributeValue.fromS(moduleVersion.getVersion());
    }

    @Override
    public ArtifactVersion transformTo(AttributeValue attributeValue) {
      return new ArtifactVersion(attributeValue.s());
    }

    @Override
    public EnhancedType<ArtifactVersion> type() {
      return EnhancedType.of(ArtifactVersion.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
      return AttributeValueType.S;
    }
  }
}
