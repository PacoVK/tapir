### Deploy-Keys


Deploy Keys are used to authenticate upload requests. When creating a deploy key, you need to provide the following parameters:

- **`resource-type`**: Specifies whether the deploy key is for a `module` or a `provider`.
- **`source`**: The location of the module or provider, typically a Git repository.
- **`scope`**: Defines the access level for the deploy key, allowing fine-grained control.
- **`namespace`**: The namespace of the resource (applies to both `module` and `provider`).
- **`name`**: The name of the resource (applies to `module`).
- **`type`**: The type of resource (applies to `provider`).
- **`provider`**: The provider of the resource (applies to `module`).

#### Scope

The scope parameter determines the hierarchy of access. You can choose the level of granularity for the deploy key based on the resource type.

Resource hierarchy:
- **Module**: `<namespace>/<name>/<provider>`
- **Provider**: `<namespace>/<type>`

Depending on the resource type, you can set the scope to one of these levels. The deploy key will grant access to all resources under the specified scope.

##### Examples

Given the following resources:

- `tapir/networking/aws`
- `tapir/networking/azure`
- `tapir/compute/aws`
- `tapir/compute/azure`
- `anta/networking/aws`
- `anta/networking/azure`
- `anta/compute/aws`
- `anta/compute/azure`

1. **Scope: `namespace` (e.g., `namespace == tapir`)**

   A deploy key with this scope would grant access to all `tapir` resources:

  - `tapir/networking/aws`
  - `tapir/networking/azure`
  - `tapir/compute/aws`
  - `tapir/compute/azure`

2. **Scope: `name` (e.g., `namespace == anta`, `name == compute`)**

   This scope limits access to the `compute` resources within the `anta` namespace:

  - `anta/compute/aws`
  - `anta/compute/azure`

3. **Scope: `provider` (e.g., `namespace == tapir`, `name == networking`, `provider == azure`)**

   This would restrict access to only the `tapir/networking/azure` module.

  - `tapir/networking/azure`

#### Legacy Deploy keys.

Before version `0.9`, deploy keys were not **scopable**. While it is recommended to migrate to scopable keys, legacy deploy keys remain valid. They are treated as the most restrictive scope (`provider` for `modules` and `type` for `providers`), limiting access to a specific resource.

### Upload a module or provider

To upload a module or provider you need to be authenticated via [DeployKey](#deploy-keys)

#### Upload a module

When you publish a Terraform module, you need a [DeployKey](#deploy-keys) giving permission to the new module.

##### Prerequisites:
* The package name and version must be unique in the top-level namespace.
* You need to specify a module namespace, a module name and the modules corresponding provider. For example `myorg/vpc/aws`.
* Versioning must follow [Semantic Versioning](https://semver.org) specs
* Currently only `.zip` is supported.

**NOTE**: The zipped module directory layout should follow the [Terraform module structure](https://www.terraform.io/docs/language/modules/develop/structure.html).

Example:
```
module.zip
├── main.tf
├── outputs.tf
├── README.md
├── variables.tf
└── <any-other-file-or-directory>
```

##### What Tapir does on upload

* It will optimize the module
    * Folder that names contain `example` will be removed
    * Hidden files and folders will be removed
    * System files and folders will be removed (eg. files that names  contain `MACOS`)
* It will scan the module source code for security vulnerabilities and code quality issues
    * Tapir integrates [Trivy](https://trivy.dev/) for that purpose
* It will generate documentation and stats for the module
    * See module dependencies, inputs, outputs and resources that will be generated
    * Tapir integrates [terraform-docs](https://terraform-docs.io/) for that purpose

##### Upload via API

You can upload modules to the registry via its HTTP REST-Api. It will return HTTP status `200` on success.
```shell
curl -XPOST  -H 'x-api-key: <API_KEY>' --fail-with-body -F archive=@archive.zip "https://example.corp.com/terraform/modules/v1/<namespace>/<name>/<provider>/<version>"
```
> Tapir has build-in support for several module providers. This means you should follow the naming convention for specific module provider:
>
> **AWS:** aws <br/>
> **Azure:** azurerm <br/>
> **Google:** google <br/>
> **Kubernetes:** kubernetes <br/>

#### Upload a provider
When you publish a Terraform provider, you need a [DeployKey](#deploy-keys) giving permission to the new provider.


Looking for the [troubleshooting docs](./TROUBLESHOOT.md)?

To create and build the provider it is highly recommended to use the [official HashiCorp provider project template](https://github.com/hashicorp/terraform-provider-scaffolding). It uses [goreleaser](https://goreleaser.com/) to sign the actual provider binaries. For details see [how to prepare release](https://developer.hashicorp.com/terraform/registry/providers/publishing#preparing-your-provider).

##### Prerequisites:
* The provider name (aka. type) must be unique in the top-level namespace.
* You need to specify a provider namespace, a provider type. For example `myorg/my-provider`.
* Versioning must follow [Semantic Versioning](https://semver.org) specs
* Currently only `.zip` is supported.
* The `.zip` must contain all files that are described in [how to prepare release](https://developer.hashicorp.com/terraform/registry/providers/publishing#preparing-your-provider).

##### Upload via API

You can upload provider to the registry via its HTTP REST-Api. It will return HTTP status `200` on success.
```shell
curl -XPOST -H 'x-api-key: <API_KEY>' --fail-with-body -F archive=@archive.zip "https://example.corp.com/terraform/providers/v1/<namespace>/<type>/<version>"
```

### Reference a Terraform Module or provider

**Prerequisites**:
* Terraform registry needs to run with HTTPS, since Terraform does not support HTTP registries
* If the registry runs on another port that `443` you need to specify the port

You don't need to specify the protocol explicit.

####  Reference a module

```hcl
module "my-module" {
  source = "example.corp.com/<namespace>/<name>/<provider>"
  version = "<version>"
}
```

#### Reference a provider

```hcl
terraform {
  required_providers {
    foo = {
      source = "example.corp.com/<namespace>/<type>"
    }
  }
}


provider "foo" {
  # Configuration options
}
```