package backend.aws.dynamodb.converter;

import core.terraform.ModuleVersion;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.ListAttributeConverter;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collection;
import java.util.LinkedList;

public class ModuleVersionsConverter implements AttributeConverter<Collection<ModuleVersion>> {

  private final ListAttributeConverter<Collection<ModuleVersion>> listAttributeConverter = ListAttributeConverter
          .builder(EnhancedType.collectionOf(ModuleVersion.class))
          .collectionConstructor(LinkedList::new)
          .elementConverter(new ModuleVersionsConverter.ModuleVersionConverter()).build();
  ;

  @Override
  public AttributeValue transformFrom(Collection<ModuleVersion> moduleVersions) {
    return listAttributeConverter.transformFrom(moduleVersions);
  }

  @Override
  public Collection<ModuleVersion> transformTo(AttributeValue attributeValue) {
    return listAttributeConverter.transformTo(attributeValue);
  }

  @Override
  public EnhancedType<Collection<ModuleVersion>> type() {
    return listAttributeConverter.type();
  }

  @Override
  public AttributeValueType attributeValueType() {
    return listAttributeConverter.attributeValueType();
  }

  static class ModuleVersionConverter implements AttributeConverter<ModuleVersion> {

    @Override
    public AttributeValue transformFrom(ModuleVersion moduleVersion) {
      return AttributeValue.fromS(moduleVersion.getVersion());
    }

    @Override
    public ModuleVersion transformTo(AttributeValue attributeValue) {
      return new ModuleVersion(attributeValue.s());
    }

    @Override
    public EnhancedType<ModuleVersion> type() {
      return EnhancedType.of(ModuleVersion.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
      return AttributeValueType.S;
    }
  }
}
