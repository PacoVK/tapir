# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Tapir is a private Terraform registry implementation built on Quarkus (Java) and React. It implements the official Terraform registry protocol for both modules and providers, with features including code scanning (Trivy), documentation generation (terraform-docs), search capabilities, and authentication via OIDC.

## Build and Development Commands

### Initial Setup
```bash
# With Homebrew
brew bundle && make bootstrap

# Linux without Homebrew - ensure you have: mkcert, nss (for Firefox), awscli, terraform
make bootstrap
```

### Development Workflow
```bash
# Start local dev environment (docker-compose services)
make dev

# Start backend in dev mode (includes Quarkus dev UI at http://localhost:8080/q/dev/)
make backend

# Full build
./mvnw clean install

# Build without tests
./mvnw clean install -DskipTests

# Run backend tests only
./mvnw test

# Run single backend test
./mvnw test -Dtest=ClassName#methodName

# Clean everything (removes docker volumes and build artifacts)
make clean
```

### Frontend Development
```bash
cd src/main/webui

# Install dependencies
yarn install

# Run tests
yarn test

# Run linter
yarn lint

# Start dev server (when running alongside Quarkus)
yarn start
```

### Terraform Example Testing
```bash
# Initialize terraform with local Tapir registry
make terraform
```

## Architecture

### Backend Architecture (Quarkus/Java)

**Layered Structure:**
- `api/` - REST API layer implementing Terraform registry protocol
  - `api/auth/` - OIDC authentication + API key (Deploy-Keys) mechanism
  - `api/dto/` - Data transfer objects and mappers
  - `api/router/SpaRouter.java` - **Important**: Handles SPA routing workaround for Quinoa
- `core/` - Core business logic
  - `core/backend/` - Pluggable database adapters (DynamoDB, Elasticsearch, CosmosDB)
  - `core/storage/` - Pluggable storage adapters (S3, Azure Blob, Local)
  - `core/service/` - Business services
  - `core/upload/` - File upload handling
  - `core/terraform/` - Terraform-specific logic
  - `core/vertx/` - Vert.x event handling
- `extensions/` - Feature extensions
  - `extensions/security/` - Trivy integration for security scanning
  - `extensions/docs/` - terraform-docs integration
  - `extensions/cli/` - CLI command processing

**Key Patterns:**
- Repository pattern with interface `IRepository` and implementations per backend
- Storage abstraction via `StorageRepository` interface
- Extension-based architecture for security scanning and documentation generation
- Event-driven processing using Vert.x for async operations

### Frontend Architecture (React + TypeScript)

- Located in `src/main/webui/`
- Material-UI (MUI) component library
- React Router for navigation
- Integrated with Quarkus via Quinoa extension
- Served at http://localhost:8080 (dev mode: separate dev server on port 3000)

### Configuration

Primary config: `src/main/resources/application.yml`
- Uses Quarkus profiles: `%dev`, `%test`, `%prod` (default)
- Environment variable substitution pattern: `${VAR_NAME:default_value}`
- Multi-backend support: DynamoDB (default), Elasticsearch, CosmosDB
- Multi-storage support: S3 (default), Azure Blob, Local filesystem

**When adding new config parameters:**
1. Add to `application.yml`
2. Update [Helm chart values.yaml](https://github.com/PacoVK/tapir-helm/blob/main/charts/values.yaml)
3. Update [Helm README template](https://github.com/PacoVK/tapir-helm/blob/main/charts/README.md.gotmpl)

### Important Implementation Details

**API Routing Workaround:**
When adding new API routes, you MUST update the `PATH_PREFIXES` array in `src/main/java/api/router/SpaRouter.java`. This is required due to Quinoa SPA routing limitations. Any new API prefix needs to be added to prevent SPA routing conflicts.

Current API prefixes: `/q/`, `/terraform/`, `/static/`, `/tapir/`, `/search/`, `/reports/`, `/management/deploykey/`, `/.well-known/`

**Authentication:**
- OIDC for UI/management (requires `admin` role in token)
- Deploy-Keys for CI/CD REST API access (module/provider publishing)
- Reading from registry works without authentication (Terraform CLI compatible)

**Testing Requirements:**
- Backend tests require LocalStack (S3, DynamoDB), OpenSearch, and Azurite running
- Frontend tests run with `yarn test` in `src/main/webui/`
- Use `@QuarkusTest` annotation for integration tests
- Abstract test classes available: `AbstractModuleBackendTest`, `AbstractProviderBackendTest`, `AbstractStorageTest`

## Code Quality

- **Checkstyle**: Google style guide enforced via `maven-checkstyle-plugin`
- **Code Coverage**: JaCoCo with exclusions for DTOs, mappers, Terraform models
- **Test Coverage**: Both unit and integration tests expected for new features

## Health Checks

Tapir exposes health check endpoints without authentication for monitoring and orchestration:

**Endpoints:**
- `/q/health` - Combined health status (all checks)
- `/q/health/live` - Liveness probe (basic application is running)
- `/q/health/ready` - Readiness probe (application + dependencies ready)

**Custom Health Checks:**
- **DatabaseHealthCheck** (`core/backend/DatabaseHealthCheck.java`): Verifies active backend (DynamoDB, Elasticsearch, or CosmosDB) is properly initialized
- **StorageHealthCheck** (`core/storage/StorageHealthCheck.java`): Validates active storage backend (S3, Azure Blob, or Local filesystem) is properly initialized

**Testing Health Checks:**
```bash
# After starting backend with `make backend`
curl http://localhost:8080/q/health
curl http://localhost:8080/q/health/ready
curl http://localhost:8080/q/health/live
```

Health checks are configured to:
- Use `/q/health/*` path (standard Quarkus management interface)
- Bypass authentication (permit policy)
- Return JSON with status and check details

## Common Pitfalls

1. **SpaRouter**: Forgetting to add new API routes to `PATH_PREFIXES` causes routing failures
2. **Multi-backend support**: Changes to data models must work across DynamoDB, Elasticsearch, and CosmosDB
3. **Multi-storage support**: File operations must work across S3, Azure Blob, and Local storage
4. **Quarkus Dev Mode**: Use `./mvnw quarkus:dev` not `./mvnw compile quarkus:dev`
5. **Frontend integration**: Quinoa handles frontend building during Maven package phase
