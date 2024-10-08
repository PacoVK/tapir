package core.tapir;

import core.terraform.Module;
import core.terraform.Provider;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class DeployKey extends CoreEntity {
  String resourceType;
  DeployKeyScope scope;
  String source;
  String namespace;
  String provider;
  String name;
  String type;
  String key;
  String id;
  Instant lastModifiedAt;

  public DeployKey() {}

  public DeployKey(DeployKeyScope scope, String source, String namespace, String name, String provider, String key) {
    this.scope = scope;
    this.source = source;
    this.namespace = namespace;
    this.name = name;
    this.provider = provider;
    this.resourceType = "module";
    this.key = key;
    setId(new ArrayList<>(Arrays.asList(this.resourceType, this.namespace, this.name, this.provider)));
  }

  public DeployKey(DeployKeyScope scope, String source, String namespace, String type, String key) {
    this.scope = scope;
    this.source = source;
    this.namespace = namespace;
    this.type = type;
    this.resourceType = "provider";
    this.key = key;
    setId(new ArrayList<>(Arrays.asList(this.resourceType, this.namespace, this.type)));
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setId(ArrayList<String> idTokens) {
    idTokens.removeAll(Arrays.asList("", null));
    this.id = String.join("-", idTokens);
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Instant getLastModifiedAt() {
    return lastModifiedAt;
  }

  public void setLastModifiedAt(Instant lastModifiedAt) {
    this.lastModifiedAt = lastModifiedAt;
  }

  public DeployKeyScope getScope() {
    return scope;
  }

  public String getResourceType() {
    return resourceType;
  }

  public String getNamespace() {
    return namespace;
  }

  public String getProvider() {
    return provider;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public boolean ValidForModule(Module module) {
    if (!Objects.equals(this.resourceType, "module")) {
      return false;
    }
    // Let's support the legacy keys
    if (this.scope == null) {
      // Let's reconstruct the expected key id
      String expectedKeyId = String.format("%s-%s-%s", module.getNamespace(), module.getName(), module.getProvider());
      return Objects.equals(this.id, expectedKeyId);
    }
    // Let's first validate the namespace
    if (!Objects.equals(this.namespace, module.getNamespace())) {
      return false;
    }
    if (Objects.equals(this.scope, DeployKeyScope.NAMESPACE)) {
      // no need to go furter
      return true;
    }
    if (!Objects.equals(this.name, module.getName())) {
      return false;
    }
    if (Objects.equals(this.scope, DeployKeyScope.NAME)) {
      // no need to go furter
      return true;
    }
    return !Objects.equals(this.provider, module.getProvider());
  }

  public boolean ValidForProvider(Provider provider) {
    if (!Objects.equals(this.resourceType, "provider")) {
      return false;
    }
    // Let's support the legacy keys
    if (this.scope == null) {
      // Let's reconstruct the expected key id
      String expectedKeyId = String.format("%s-%s", provider.getNamespace(), provider.getType());
      return Objects.equals(this.id, expectedKeyId);
    }
    if (!Objects.equals(this.namespace, provider.getNamespace())) {
      return false;
    }
    if (Objects.equals(this.scope,  DeployKeyScope.NAMESPACE)) {
      // no need to check further
      return true;
    }
    return Objects.equals(this.type, provider.getType());
  }
}
