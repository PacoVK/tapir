# Tapir
### A Private Terraform Registry

![Tapir overview](./docs/images/tapir.gif)

Tapir is the registry you always wanted if you are using Terraform at enterprise scale.
Core values of Tapi is to provide
* visibility
* transparency
* increases adoption rate
* security
* quality for your Terraform modules.

## Why?
Terraform modules are reusable parts of infrastructure code. The most crucial part of re-usability is transparency and visibility. Since Terraform supports Git-based modules there are several disadvantages that come along with this capability.
* Access to Git repos are often designed on team level, no access for others per default
* Search capabilities are very limited, in terms you are searching for specific Terraform modules
* You may not get insights in the codes quality and security measures
* Module versioning is not enforced
  This is where Tapir jumps in.

## About Tapir
Tapir is an implementation of the [official Terraform registry protocol](https://developer.hashicorp.com/terraform/internals/module-registry-protocol).
You can easily run an instance on your own with the full flexibility and power a central registry has.
* It will provide you a simple, but powerful UI to search for modules that are available
  across your organization.
* It implements the official Terraform registry protocols
  * currently only modules supported
* It scans the source code on push, you will have insights about the code quality and security measures
  * Tapir integrates [Tfsec](https://aquasecurity.github.io/tfsec) for that purpose
* It provides several storage adapters for modules
  * currently S3 and AzureBlob
* It provides several database adapters for the data
  * currently Dynamodb (default), Elasticsearch
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

| Variable                        | Description                                                                                                                               | Required                                | Default      |
|---------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|--------------|
| BACKEND_CONFIG                  | The database to make use of                                                                                                               | X                                       | dynamodb     |
| BACKEND_ELASTICSEARCH_HOST      | Host of the Elasticsearch instance                                                                                                        | Yes, if BACKEND_CONFIG is elasticsearch |              |
| STORAGE_CONFIG                  | The blob storage to make use of                                                                                                           | X                                       | s3           |
| STORAGE_ACCESS_SESSION_DURATION | Amount of minutes the signed download url is valid                                                                                        |                                         | 5            |
| AZURE_BLOB_CONNECTION_STRING    | [Connection string](https://learn.microsoft.com/en-us/azure/storage/common/storage-configure-connection-string) to use for authentication | Yes, if STORAGE_CONFIG is azureBlob     |              |
| AZURE_BLOB_CONTAINER_NAME       | Blob container name to be used to store module archives                                                                                   | Yes, if STORAGE_CONFIG is azureBlob     | tf-registry  |
| S3_STORAGE_BUCKET_NAME          | S3 bucket name to be used to store module archives                                                                                        | Yes, if STORAGE_CONFIG is s3            | tf-registry  |
| S3_STORAGE_BUCKET_REGION        | AWS region of the target S3 bucket                                                                                                        | Yes, if STORAGE_CONFIG is s3            | eu-central-1 |

### Upload a module
When you publish a Terraform Module, if it does not exist, it is created.

**Prerequisites**:
* The package name and version must be unique in the top-level namespace.
* You need to specify a module namespace, a module name and the modules corresponding provider. For example `myorg/vpc/aws`.
* Versioning must follow [Semantic Versioning](https://semver.org) specs
* Currently only `.zip` is supported.

You can simply upload modules to the registry via its HTTP REST-Api. It will return HTTP status `200` on success.
```shell
curl -XPOST --fail-with-body -F module_archive=@archive.zip "https://example.corp.com/terraform/modules/v1/<namespace>/<name>/<provider>/<version>"
```
> Tapir has build-in support for several module providers. This means you should follow the naming convention for specific module provider:
> 
> **AWS:** aws <br/>
> **Azure:** azurerm <br/>
> **Google:** google <br/>
> **Kubernetes:** kubernetes <br/>

### Reference a Terraform Module

**Prerequisites**:
* Terraform registry needs to run with HTTPS, since Terraform does not support HTTP registries
* If the registry runs on another port that `443` you need to specify the port

You don't need to specify the protocol explicit.
```hcl
module "my-module" {
  source = "example.corp.com/<namespace>/<name>/<provider>"
  version = "<version>"
}
```

## Contribution

A detailed How-to guide on local development can be found in the [docs](./docs/RUNBOOK.md).

**Actively searching** for contributors.
**Feedback** is always appreciated :rainbow:
Feel free to open an Issue (Bug- /Feature-Request)
or provide a Pull request. :wrench: