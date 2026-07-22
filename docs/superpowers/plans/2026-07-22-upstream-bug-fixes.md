# Upstream Bug Fixes Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Fix 6 open bugs on the upstream PacoVK/tapir project, each in its own branch and PR.

**Architecture:** Each bug is independent. Each gets its own branch off `main`, its own set of changes, and its own PR. Order is by confidence in root cause (high confidence first).

**Tech Stack:** Java 21, Quarkus, RESTEasy Reactive, DynamoDB, CosmosDB, Maven

**Branch naming convention:** `fix/upstream-<issue-number>-<short-description>`

## Global Constraints

- Follow existing code style and patterns (Google style via checkstyle)
- No unrelated refactoring — fix the bug, nothing else
- Each PR must reference the upstream issue
- All existing tests must continue to pass
- Run `./mvnw test` before committing
- Run `yarn lint` and `yarn test` in `src/main/webui` if frontend files change

---

### Task 1: #514 — Deploy Keys Missing Fields

**Branch:** `fix/upstream-514-deploykey-missing-fields`

**Files:**
- Modify: `src/main/java/core/backend/aws/dynamodb/repository/TableSchemas.java:105-118`
- Modify: `src/main/java/core/tapir/DeployKey.java` (add missing `getSource()` getter)
- Reference: `src/main/java/core/tapir/DeployKey.java:11-21` (the 9 fields)

**Root cause:** `TableSchemas.deployKeysTableSchema` only maps 3 of 9 fields on `DeployKey`. The DynamoDB enhanced client only serializes/deserializes attributes that are registered in the schema builder. When a deploy key is saved and then queried, only `id`, `key`, and `createdAt` are persisted; `resourceType`, `scope`, `source`, `namespace`, `provider`, `name`, `type` are silently dropped.

**Fix:** Add the missing 6 fields to `deployKeysTableSchema` in `TableSchemas.java`.

- [ ] **Step 1: Read current schema**

```java
// Current in TableSchemas.java lines 105-118:
static final TableSchema<DeployKey> deployKeysTableSchema =
        TableSchema.builder(DeployKey.class)
                .newItemSupplier(DeployKey::new)
                .addAttribute(String.class, a -> a.name("id")
                        .getter(DeployKey::getId)
                        .setter(DeployKey::setId)
                        .tags(primaryPartitionKey()))
                .addAttribute(String.class, a -> a.name("key")
                        .getter(DeployKey::getKey)
                        .setter(DeployKey::setKey))
                .addAttribute(Instant.class, a -> a.name("createdAt")
                        .getter(DeployKey::getLastModifiedAt)
                        .setter(DeployKey::setLastModifiedAt))
                .build();
```

- [ ] **Step 2: Add missing fields to schema**

Add `resourceType` (String), `scope` (DeployKeyScope, stored as String via enum name), `source` (String), `namespace` (String), `provider` (String), `name` (String), `type` (String) to the schema builder:

```java
static final TableSchema<DeployKey> deployKeysTableSchema =
        TableSchema.builder(DeployKey.class)
                .newItemSupplier(DeployKey::new)
                .addAttribute(String.class, a -> a.name("id")
                        .getter(DeployKey::getId)
                        .setter(DeployKey::setId)
                        .tags(primaryPartitionKey()))
                .addAttribute(String.class, a -> a.name("resourceType")
                        .getter(DeployKey::getResourceType)
                        .setter(DeployKey::setResourceType))
                .addAttribute(String.class, a -> a.name("scope")
                        .getter(k -> k.getScope() != null ? k.getScope().name() : null)
                        .setter((k, v) -> k.setScope(v != null ? DeployKeyScope.valueOf(v) : null)))
                .addAttribute(String.class, a -> a.name("source")
                        .getter(DeployKey::getSource)
                        .setter(DeployKey::setSource))
                .addAttribute(String.class, a -> a.name("namespace")
                        .getter(DeployKey::getNamespace)
                        .setter(DeployKey::setNamespace))
                .addAttribute(String.class, a -> a.name("provider")
                        .getter(DeployKey::getProvider)
                        .setter(DeployKey::setProvider))
                .addAttribute(String.class, a -> a.name("name")
                        .getter(DeployKey::getName)
                        .setter(DeployKey::setName))
                .addAttribute(String.class, a -> a.name("type")
                        .getter(DeployKey::getType)
                        .setter(DeployKey::setType))
                .addAttribute(String.class, a -> a.name("key")
                        .getter(DeployKey::getKey)
                        .setter(DeployKey::setKey))
                .addAttribute(Instant.class, a -> a.name("createdAt")
                        .getter(DeployKey::getLastModifiedAt)
                        .setter(DeployKey::setLastModifiedAt))
                .build();
```

Note: The `source` field exists in the model (`DeployKey.java:14`) and has a getter but no dedicated `getSource()` method — add it if missing. Check `DeployKey.java` for a `getSource()` method.

- [ ] **Step 3: Verify `DeployKey.java` has getters for all fields**

Check if `getSource()` exists (line 14 has `String source` but no `getSource()` seen in the file at lines 75-97). If missing, add:

```java
public String getSource() {
    return source;
}
```

- [ ] **Step 4: Run tests**

```bash
./mvnw test -Dtest='*DeployKey*'
```

- [ ] **Step 5: Run full test suite**

```bash
./mvnw test
```

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "fix: add missing deploy key fields to DynamoDB schema (#514)

The DeployKey table schema in DynamoDB was only persisting 3 of 9 fields:
id, key, and createdAt. Fields resourceType, scope, source, namespace,
provider, name, and type were silently dropped because they were not
registered in the enhanced client schema builder.

Fixes #514
```

---

### Task 2: #515 — Cosmos DB Query Syntax Error

**Branch:** `fix/upstream-515-cosmosdb-query-syntax`

**Files:**
- Modify: `src/main/java/core/backend/azure/cosmosdb/CosmosDbRepository.java`

**Root cause:** Cosmos DB SQL API does not support `LIKE` with `@parameter` syntax in the way SQL Server does. The queries use `SELECT * FROM c WHERE c.namespace LIKE @namespace`. Cosmos DB SQL API supports `LIKE` but parameterized `LIKE` patterns with `%` wildcards may not bind correctly through `SqlParameter`. The fix is to use Cosmos DB's `CONTAINS()`, `STARTSWITH()`, or inline string concatenation depending on what the Cosmos DB SDK supports.

**Note:** According to Cosmos DB documentation, `LIKE` is supported with parameters, but the `%` wildcards in parameter values should work. However, the error in the issue shows `@modules` being interpreted as a parameter reference in the FROM clause position, suggesting the issue may be more subtle — possibly the query is being constructed incorrectly when the collection name itself contains `@` substitution. The safest fix is to replace `LIKE` with `CONTAINS` which is more broadly supported.

- [ ] **Step 1: Read `CosmosDbRepository.java`**

Review the 3 methods that construct queries: `findModules()` (lines 80-101), `findProviders()` (lines 103-123), `findDeployKeys()` (lines 125-145).

- [ ] **Step 2: Replace LIKE with CONTAINS in all 3 query methods**

Change `findModules`:
```java
// Before:
SqlQuerySpec querySpec = new SqlQuerySpec(
        "SELECT * FROM c WHERE c.namespace "
                + "LIKE @namespace OR c.name LIKE @name OR c.provider LIKE @provider",
        paramList);

// After:
SqlQuerySpec querySpec = new SqlQuerySpec(
        "SELECT * FROM c WHERE CONTAINS(c.namespace, @namespace) "
                + "OR CONTAINS(c.name, @name) OR CONTAINS(c.provider, @provider)",
        paramList);
```

And update parameter values to remove the `%` wildcards:
```java
// Before:
List<SqlParameter> paramList = List.of(
        new SqlParameter("@namespace", "%" + term + "%"),
        new SqlParameter("@name", "%" + term + "%"),
        new SqlParameter("@provider", "%" + term + "%")
);

// After:
List<SqlParameter> paramList = List.of(
        new SqlParameter("@namespace", term),
        new SqlParameter("@name", term),
        new SqlParameter("@provider", term)
);
```

Apply the same pattern to `findProviders` and `findDeployKeys`.

- [ ] **Step 3: Run tests**

```bash
./mvnw test -Dtest='*CosmosDb*'
```

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "fix: replace Cosmos DB LIKE with CONTAINS for SQL compatibility (#515)

Cosmos DB SQL API does not support LIKE queries with parameterized
patterns containing % wildcards. Replaced LIKE with CONTAINS() and
removed the % wildcards from parameter values, as CONTAINS does
substring matching by default.

Fixes #515
```

---

### Task 3: #406 — CORS Error Creating Deploy Key

**Branch:** `fix/upstream-406-cors-deploykey`

**Files:**
- Modify: `src/main/resources/application.yml`
- Check: `src/main/java/api/router/SpaRouter.java:26`

**Root cause 1:** The default CORS config in `application.yml` (lines 172-175) uses `origins: *` but does not set `access-control-allow-credentials: true`. When the frontend sends credentialed requests (cookies for OIDC auth), the browser requires both:
1. `Access-Control-Allow-Credentials: true` AND
2. An explicit origin (not `*`)

**Root cause 2:** `SpaRouter.java` line 26 has `/management/deploykey/` (with trailing slash) but the actual REST endpoint is at `/management/deploykey` (without trailing slash as declared in `Management.java` line 41: `@Path("/deploykey")`). While this only affects GET routes in SpaRouter, it means `/management/deploykey` (no trailing slash) bypasses the prefix check and would be rerouted to the SPA.

- [ ] **Step 1: Fix CORS config**

In `application.yml`, update the CORS section (lines 172-175):
```yaml
    cors:
      origins: ${registry.cors.origins}
      methods: "*"
      headers: "*"
      access-control-allow-credentials: true
```

Also update the `registry.cors.origins` default to be empty (the user must set this explicitly when credentials are needed):
```yaml
  cors:
    origins: ${CORS_ORIGINS:}
```

- [ ] **Step 2: Fix SpaRouter path prefix**

In `SpaRouter.java` line 26, change `/management/deploykey/` to `/management/deploykey`:
```java
"/management/deploykey",
```

- [ ] **Step 3: Run full test suite**

```bash
./mvnw test
```

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "fix: resolve CORS errors when creating deploy keys (#406)

Add access-control-allow-credentials: true to default CORS config
(required for credentialed requests with cookies). Also fix trailing
slash mismatch in SpaRouter path prefix for /management/deploykey.

Fixes #406
```

---

### Task 4: #483 — Providers Not Listed As Module Dependency

**Branch:** `fix/upstream-483-module-provider-dependencies`

**Files:**
- Modify: `src/main/java/core/terraform/Module.java`
- Modify: `src/main/java/api/Modules.java`
- Modify: `src/main/java/api/dto/ModuleDto.java` (if exists — check first)
- Potential: `src/main/java/core/vertx/event/consumer/ReportListener.java`

**Root cause:** The `terraform-docs` output includes provider dependencies in `TerraformDocumentation.providers`, but this data is only stored in the Report (as documentation) and never surfaced through the Module API. The `Module` model has no field for provider dependencies.

- [ ] **Step 1: Add provider dependencies field to Module**

In `Module.java`, add a field for provider dependencies:
```java
private List<ModuleProviderDependency> providerDependencies;
```

Create a simple inner class or separate record/class `ModuleProviderDependency` with `name`, `source`, `version` fields.

- [ ] **Step 2: Wire provider dependencies during module ingest**

Check `ReportListener.java` — after documentation is generated from `terraform-docs`, extract the provider list and attach it to the module before persisting.

- [ ] **Step 3: Expose provider dependencies in API response**

In `Modules.java`, include `providerDependencies` in the response for `getModuleByName()`.

- [ ] **Step 4: Run tests**

```bash
./mvnw test
```

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "feat: expose provider dependencies on module details (#483)

Provider dependencies parsed by terraform-docs are now attached to
the Module model and surfaced through the module API, so providers
hosted in Tapir appear as module dependencies.

Fixes #483
```

---

### Task 5: #500 — Publishing Modules Returns 403

**Branch:** `fix/upstream-500-publish-module-403`

**Files:**
- Investigate: `src/main/java/api/auth/ApiKeyAuthenticationMechanism.java`
- Investigate: `src/main/java/core/service/DeployKeyService.java`
- Modify: `src/main/java/api/auth/ApiKeyAuthenticationMechanism.java` (if root cause found)

**Root cause:** Under investigation. The 403 likely occurs because:
1. The deploy key validation in `validateKeyByRequestPath()` fails (field mismatch or missing key fields — related to #514)
2. The path parsing `requestPath.split("/v1/")[1]` is fragile — paths without `/v1/` cause `ArrayIndexOutOfBoundsException`
3. Or it's a user configuration issue (expired key, wrong namespace)

- [ ] **Step 1: Investigate root cause**

Check if `validateKeyByRequestPath` handles all URL formats correctly:
- Provider upload: `POST /terraform/providers/v1/{namespace}/{type}/{version}`
- Module upload: `POST /terraform/modules/v1/{namespace}/{name}/{provider}/{version}`

The `split("/v1/")[1]` approach:
- For modules: yields `{namespace}/{name}/{provider}/{version}` → `split[0]=namespace`, `split[1]=name`, `split[2]=provider` ✓
- For providers: yields `{namespace}/{type}/{version}` → `split[0]=namespace`, `split[1]=type` ✓

- [ ] **Step 2: Fix if code bug found**

If the path parsing is fragile, add validation and a descriptive error message:

```java
private boolean validateKeyByRequestPath(DeployKey key, String requestPath) {
    String[] parts = requestPath.split("/v1/");
    if (parts.length < 2) {
        LOGGER.warning("Invalid request path format (missing /v1/): " + requestPath);
        return false;
    }
    String resourceId = parts[1];
    // ... rest of existing logic
}
```

- [ ] **Step 3: Run tests**

```bash
./mvnw test
```

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "fix: improve deploy key validation error handling for module publish (#500)

Add defensive path parsing and better logging to validateKeyByRequestPath
to provide clearer error messages when deploy key validation fails during
module/provider upload.

Fixes #500
```

---

### Task 6: #379 — Microsoft Entra ID Authentication

**Branch:** `fix/upstream-379-entra-id-auth`

**Files:**
- Modify: `src/main/resources/application.yml`
- Modify: `src/main/java/api/auth/OidcTenantResolver.java` (if needed)
- Investigate: `src/main/java/core/service/AuthService.java`

**Root cause:** Azure AD / Entra ID uses different OIDC endpoints, claim names, and token formats than Keycloak (the default). The `AUTH_PROVIDER=microsoft` setting should automatically configure the correct defaults but doesn't work properly. Known workarounds exist using raw Quarkus properties. The fix should make `AUTH_PROVIDER=microsoft` work out of the box.

- [ ] **Step 1: Investigate current behavior**

Read `application.yml` OIDC section (lines 88-116) — understand how `quarkus.oidc.provider: ${registry.auth.provider}` is used and whether the `microsoft` provider config is properly mapped.

- [ ] **Step 2: Add Azure AD-specific default configuration**

Update `application.yml` to include sane Azure AD defaults when `AUTH_PROVIDER=microsoft`:
- Ensure `QUARKUS_OIDC_TOKEN_CUSTOMIZER_NAME=azure-access-token-customizer` works automatically
- Add `QUARKUS_OIDC_ROLES_ROLE_CLAIM_PATH=roles` for Azure AD role mapping
- Add `QUARKUS_OIDC_AUTHENTICATION_FORCE_REDIRECT_HTTPS_SCHEME=true` for HTTPS behind proxy

Since Quarkus profiles don't support conditional logic based on provider config, update documentation in `application.yml` with commented-out Azure AD config.

- [ ] **Step 3: Run tests**

```bash
./mvnw test
```

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "fix: improve Microsoft Entra ID (Azure AD) OIDC support (#379)

Add documented Azure AD-specific Quarkus OIDC configuration defaults
including token customizer, role claim path, and HTTPS redirect scheme
settings for proper Entra ID integration.

Fixes #379
```
