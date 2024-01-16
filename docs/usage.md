### Upload a module or provider

To upload a module or provider you need to be authenticated via DeployKey. A DeployKey can be created and managed by Tapir administrators (see [authentication prerequisites](./configuration.md#Prerequisites)). After creating deploy keys, you can use them to authenticate against the REST-API. There is one DeployKey per namespace.

#### Upload a module

When you publish a Terraform module, a corresponding DeployKey must be created first.

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
When you publish a Terraform provider, a corresponding DeployKey must be created first.

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