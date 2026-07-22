package core.backend.aws.dynamodb.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.terraform.Module.ModuleProviderDependency;
import java.util.List;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class ModuleProviderDependenciesConverter implements AttributeConverter<List<ModuleProviderDependency>> {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Override
  public AttributeValue transformFrom(List<ModuleProviderDependency> dependencies) {
    try {
      return AttributeValue.fromS(MAPPER.writeValueAsString(dependencies));
    } catch (Exception e) {
      throw new RuntimeException("Failed to serialize provider dependencies", e);
    }
  }

  @Override
  public List<ModuleProviderDependency> transformTo(AttributeValue attributeValue) {
    try {
      return MAPPER.readValue(attributeValue.s(),
          new TypeReference<List<ModuleProviderDependency>>() {});
    } catch (Exception e) {
      throw new RuntimeException("Failed to deserialize provider dependencies", e);
    }
  }

  @Override
  public EnhancedType<List<ModuleProviderDependency>> type() {
    return EnhancedType.listOf(ModuleProviderDependency.class);
  }

  @Override
  public AttributeValueType attributeValueType() {
    return AttributeValueType.S;
  }
}
