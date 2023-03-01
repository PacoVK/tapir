# Tapir
### A Private Terraform Registry

![Tapir overview](./docs/images/tapir.gif)

Tapir is the registry you always wanted if you are using Terraform at enterprise scale.
Core values of Tapir is to provide
* visibility
* transparency
* increases adoption rate
* security
* quality for your Terraform modules.

## Feedback 
You can send feedback and feature requests via GitHub issues. Either vote existing issues or feel free to raise an issue. 

## Why?
### Modules
Terraform modules are reusable parts of infrastructure code. The most crucial part of re-usability is transparency and visibility. Since Terraform supports Git-based modules there are several disadvantages that come along with this capability.
* Access to Git repos are often designed on team level, no access for others per default
* Search capabilities are very limited, in terms you are searching for specific Terraform modules
* You may not get insights in the codes quality and security measures
* Module versioning is not enforced
* Documentation formats vary or docs are missing at all.
  This is where Tapir jumps in.

### Providers
If you make use of custom providers, or just want to have them mirrored you need an Artifactory to store the binaries.
Additionally, users of the module need to break out the Toolchain and manually setup providers and copy them into 
the global provider directory.
Supporting Terraform providers, Tapir does not help you to get your providers visible, but also keeps the users within the toolchain of Terraform only. That means:
* Build providers with the same process and pipeline and make use of [official HashiCorp provider project template](https://github.com/hashicorp/terraform-provider-scaffolding).
* Increase security and enforce providers to be GPG signed. Running `terraform init` will check if SHASUMS are valid before downloading the actual provider binary.
* Help your users to focus on the infrastructure code rather that the setup. Tapir provides ready-to-copy code with 
a proper provider config example.

## About Tapir
Tapir is an implementation of the [official Terraform registry protocol](https://developer.hashicorp.com/terraform/internals/module-registry-protocol).
You can easily run an instance on your own with the full flexibility and power a central registry has.
* It will provide you a simple, but powerful UI to search for modules and providers that are available
  across your organization.
* It implements the official Terraform registry protocols
  * modules and providers supported
* It scans the module source code on push, you will have insights about the code quality and security measures
  * Tapir integrates [Tfsec](https://aquasecurity.github.io/tfsec) for that purpose
* It generates documentation and stats for the module 
  * See module dependencies, inputs, outputs and resources that will be generated
  * Tapir integrates [terraform-docs](https://terraform-docs.io/) for that purpose
* It provides several storage adapters
  * currently S3 and AzureBlob
* It provides several database adapters for the data
  * currently Dynamodb (default), Elasticsearch, CosmosDb
* It provides a REST-API for custom integrations and further automation
  Tapir is build on [Quarkus](https://quarkus.io/) and [ReactJS](https://reactjs.org/). You can run Tapir wherever you can run Docker images.

Apart from the above, [this is what Wikipedia knows about Tapirs](https://en.wikipedia.org/wiki/Tapir).

## Usage

### Deployment
You can run Tapir wherever you can run Docker images.
Images are available on [DockerHub](https://hub.docker.com/r/pacovk/tapir) `pacovk/tapir` and [AWS Elastic Container Registry](https://gallery.ecr.aws/pacovk/tapir) `public.ecr.aws/pacovk/tapir`.
There are samples with Terraform in `examples/`.

### Configure

You can configure Tapir passing the following environment variables:

| Variable                         | Description                                                                                                                               | Required                                | Default      |
|----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|--------------|
| BACKEND_CONFIG                   | The database to make use of                                                                                                               | X                                       | dynamodb     |
| BACKEND_ELASTICSEARCH_HOST       | Host of the Elasticsearch instance                                                                                                        | Yes, if BACKEND_CONFIG is elasticsearch |              |
| BACKEND_AZURE_MASTER_KEY         | Master key of your CosmosDb                                                                                                               | Yes, if BACKEND_CONFIG is cosmosdb      |              |
| BACKEND_AZURE_ENDPOINT           | Endpoint of your CosmosDb                                                                                                                 | Yes, if BACKEND_CONFIG is cosmosdb      |              |
| STORAGE_CONFIG                   | The blob storage to make use of                                                                                                           | X                                       | s3           |
| STORAGE_ACCESS_SESSION_DURATION  | Amount of minutes the signed download url is valid                                                                                        | X                                       | 5            |
| AZURE_BLOB_CONNECTION_STRING     | [Connection string](https://learn.microsoft.com/en-us/azure/storage/common/storage-configure-connection-string) to use for authentication | Yes, if STORAGE_CONFIG is azureBlob     |              |
| AZURE_BLOB_CONTAINER_NAME        | Blob container name to be used to store module archives                                                                                   | Yes, if STORAGE_CONFIG is azureBlob     | tf-registry  |
| S3_STORAGE_BUCKET_NAME           | S3 bucket name to be used to store module archives                                                                                        | Yes, if STORAGE_CONFIG is s3            | tf-registry  |
| S3_STORAGE_BUCKET_REGION         | AWS region of the target S3 bucket                                                                                                        | Yes, if STORAGE_CONFIG is s3            | eu-central-1 |
| API_MAX_BODY_SIZE                | The maximum payload size for module/providers to be uploaded                                                                              | X                                       | 100M         |
| REGISTRY_GPG_KEYS_0__ID          | GPG key ID of the key to be used (eg. D17C807B4156558133A1FB843C7461473EB779BD)                                                           | X                                       |              |
| REGISTRY_GPG_KEYS_0__ASCII_ARMOR | Ascii armored and bas64 encoded GPG public key (only RSA/DSA supported)                                                                   | X                                       |              |

:information_source: A note on the GPG configuration. Quarkus (and therefore Tapir) is based on [Smallrye microprofile](https://smallrye.io/smallrye-config/2.9.1/config/indexed-properties/) and supports indexed properties. Hence, you can add one or more key specifying indexed properties. See example below for passing two GPG keys (**Mind the two subsequent underscores after the index**):
```
REGISTRY_GPG_KEYS_0__ID=D17C807B4156558133A1FB843C7461473EB779BD
REGISTRY_GPG_KEYS_0__ASCII_ARMOR=LS0tLS1CRUdJTiBQR1AgUFVCTElDIEtFWSBCTE9DSy0tLS0tCgp.....tUUlO
REGISTRY_GPG_KEYS_1__ID=LS0tLS1CRUdJTiBQR1AgUFVCTElDIEtFWSDLPFKF
REGISTRY_GPG_KEYS_1__ASCII_ARMOR=LS0tLS1CRUdJTiBQR1AgUFVCTElDIEtFWSBCTE9DSy0tLS0tCgp.....JDIFH
```

### Upload a module
When you publish a Terraform module, if it does not exist, it is created.

**Prerequisites**:
* The package name and version must be unique in the top-level namespace.
* You need to specify a module namespace, a module name and the modules corresponding provider. For example `myorg/vpc/aws`.
* Versioning must follow [Semantic Versioning](https://semver.org) specs
* Currently only `.zip` is supported.

You can simply upload modules to the registry via its HTTP REST-Api. It will return HTTP status `200` on success.
```shell
curl -XPOST --fail-with-body -F archive=@archive.zip "https://example.corp.com/terraform/modules/v1/<namespace>/<name>/<provider>/<version>"
```
> Tapir has build-in support for several module providers. This means you should follow the naming convention for specific module provider:
> 
> **AWS:** aws <br/>
> **Azure:** azurerm <br/>
> **Google:** google <br/>
> **Kubernetes:** kubernetes <br/>

### Upload a provider
When you publish a Terraform provider, if it does not exist, it is created.

Looking for the [troubleshooting docs](./docs/TROUBLESHOOT.md)?

To create and build the provider it is highly recommended to use the [official HashiCorp provider project template](https://github.com/hashicorp/terraform-provider-scaffolding). It uses [goreleaser](https://goreleaser.com/) to sign the actual provider binaries. For details see [how to prepare release](https://developer.hashicorp.com/terraform/registry/providers/publishing#preparing-your-provider). 

**Prerequisites**:
* The provider name (aka. type) must be unique in the top-level namespace.
* You need to specify a provider namespace, a provider type. For example `myorg/my-provider`.
* Versioning must follow [Semantic Versioning](https://semver.org) specs
* Currently only `.zip` is supported. 
* The `.zip` must contain all files that are described in [how to prepare release](https://developer.hashicorp.com/terraform/registry/providers/publishing#preparing-your-provider).

You can simply upload provider to the registry via its HTTP REST-Api. It will return HTTP status `200` on success.
```shell
curl -XPOST --fail-with-body -F archive=@archive.zip "https://example.corp.com/terraform/providers/v1/<namespace>/<type>/<version>"
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

## Troubleshoot

See [troubleshooting docs](./docs/TROUBLESHOOT.md)

## Roadmap

* Add more storage adapter
  * GCP
  * local storage
* Add more Database adapter
  * Postgresql
* Provide a Github/ Gitlab integration to crawl for additional code metrics and ownership informations

## Contribution

A detailed How-to guide on local development can be found in the [docs](./docs/RUNBOOK.md).

**Actively searching** for contributors. <br/>
**Feedback** is always appreciated :rainbow: <br/>
Feel free to open an Issue (Bug- /Feature-Request)
or provide a Pull request. :wrench:
