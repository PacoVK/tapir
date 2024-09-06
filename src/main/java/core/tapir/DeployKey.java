package core.tapir;

import core.terraform.Module;
import core.terraform.Provider;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class DeployKey extends CoreEntity {
  String resourceType;
  String scope;
  String source;
  String namespace;
  String provider;
  String name;
  String type;
  String key;
  String id;
  Instant lastModifiedAt;

  public DeployKey() {}

  public DeployKey(String scope, String source, String namespace, String name, String provider, String key) {
    this.scope = scope;
    this.source = source;
    this.namespace = namespace;
    this.name = name;
    this.provider = provider;
    this.resourceType = "module";
    this.key = key;
    setId(new ArrayList<>(Arrays.asList(this.resourceType, this.namespace, this.name, this.provider)));
  }

  public DeployKey(String scope, String source, String namespace, String type, String key) {
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

  public boolean ValidForModule(Module module) {
    if (!Objects.equals(this.resourceType, "module")) {
      return false;
    }
    // Let's first validate the namespace
    if (!Objects.equals(this.namespace, module.getNamespace())) {
      return false;
    }
    if (Objects.equals(this.scope, "namespace")) {
      // no need to go furter
      return true;
    }
    if (!Objects.equals(this.name, module.getName())) {
      return false;
    }
    if (Objects.equals(this.scope, "name")) {
      // no need to go furter
      return true;
    }
    return !Objects.equals(this.provider, module.getProvider());
  }

  public boolean ValidForProvider(Provider provider) {
    if (!Objects.equals(this.resourceType, "provider")) {
      return false;
    }
    if (!Objects.equals(this.namespace, provider.getNamespace())) {
      return false;
    }
    if (Objects.equals(this.scope, "namespace")) {
      // no need to check further
      return true;
    }
    return Objects.equals(this.type, provider.getType());
  }
}
