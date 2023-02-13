package core.backend.aws.dynamodb.repository;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

import core.backend.aws.dynamodb.converter.ArtifactVersionsConverter;
import core.backend.aws.dynamodb.converter.ProviderPlatformsConverter;
import core.backend.aws.dynamodb.converter.SecurityReportConverter;
import core.backend.aws.dynamodb.converter.TerraformDocumentationConverter;
import core.terraform.Module;
import core.terraform.Provider;
import extensions.core.Report;
import extensions.docs.report.TerraformDocumentation;
import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class TableSchemas {

  static final TableSchema<Provider> providerTableSchema =
          TableSchema.builder(Provider.class)
                  .newItemSupplier(Provider::new)
                  .addAttribute(String.class, a -> a.name("id")
                          .getter(Provider::getId)
                          .setter(Provider::setId)
                          .tags(primaryPartitionKey()))
                  .addAttribute(String.class, a -> a.name("namespace")
                          .getter(Provider::getNamespace)
                          .setter(Provider::setNamespace))
                  .addAttribute(String.class, a -> a.name("type")
                          .getter(Provider::getType)
                          .setter(Provider::setType))
                  .addAttribute(TreeMap.class, a -> a.name("versions")
                          .getter(Provider::getVersions)
                          .setter(Provider::setVersions)
                          .attributeConverter((AttributeConverter) new ProviderPlatformsConverter())
                  )
                  .addAttribute(Instant.class, a -> a.name("published_at")
                          .getter(Provider::getPublished_at)
                          .setter(Provider::setPublished_at))
                  .build();

  static final TableSchema<Module> moduleTableSchema =
          TableSchema.builder(Module.class)
                  .newItemSupplier(Module::new)
                  .addAttribute(String.class, a -> a.name("id")
                          .getter(Module::getId)
                          .setter(Module::setId)
                          .tags(primaryPartitionKey()))
                  .addAttribute(String.class, a -> a.name("name")
                          .getter(Module::getName)
                          .setter(Module::setName))
                  .addAttribute(String.class, a -> a.name("namespace")
                          .getter(Module::getNamespace)
                          .setter(Module::setNamespace))
                  .addAttribute(String.class, a -> a.name("provider")
                          .getter(Module::getProvider)
                          .setter(Module::setProvider))
                  .addAttribute(Integer.class, a -> a.name("downloads")
                          .getter(Module::getDownloads)
                          .setter(Module::setDownloads))
                  .addAttribute(TreeSet.class, a -> a.name("versions")
                          .getter(Module::getVersions)
                          .setter(Module::setVersions)
                          .attributeConverter((AttributeConverter) new ArtifactVersionsConverter()))
                  .addAttribute(Instant.class, a -> a.name("published_at")
                          .getter(Module::getPublished_at)
                          .setter(Module::setPublished_at))
                  .build();

  static final TableSchema<Report> reportsTableSchema =
          TableSchema.builder(Report.class)
                  .newItemSupplier(Report::new)
                  .addAttribute(String.class, a -> a.name("id")
                          .getter(Report::getId)
                          .setter(Report::setId)
                          .tags(primaryPartitionKey()))
                  .addAttribute(String.class, a -> a.name("moduleName")
                          .getter(Report::getModuleName)
                          .setter(Report::setModuleName))
                  .addAttribute(String.class, a -> a.name("moduleNamespace")
                          .getter(Report::getModuleNamespace)
                          .setter(Report::setModuleNamespace))
                  .addAttribute(String.class, a -> a.name("provider")
                          .getter(Report::getProvider)
                          .setter(Report::setProvider))
                  .addAttribute(String.class, a -> a.name("moduleVersion")
                          .getter(Report::getModuleVersion)
                          .setter(Report::setModuleVersion))
                  .addAttribute(Map.class, a -> a.name("securityReport")
                          .getter(Report::getSecurityReport)
                          .setter(Report::setSecurityReport)
                          .attributeConverter((AttributeConverter) new SecurityReportConverter())
                  )
                  .addAttribute(TerraformDocumentation.class, a -> a.name("documentation")
                          .getter(Report::getDocumentation)
                          .setter(Report::setDocumentation)
                          .attributeConverter(new TerraformDocumentationConverter())
                  )
                  .build();
}
