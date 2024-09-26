package core.tapir;

public enum DeployKeyScope {
  NAMESPACE,
  NAME,
  PROVIDER,
  TYPE;


  public static DeployKeyScope fromString(String scope)  {
     if (scope == null || scope.isEmpty()) {
       return null;
     }

     for (DeployKeyScope keyScope: DeployKeyScope.values()) {
       if (keyScope.name().equalsIgnoreCase(scope)) {
         return keyScope;
       }
     }
     return null;

  }
}
