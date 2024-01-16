# Tapir
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-3-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->
### A Private Terraform Registry

[![Test](https://github.com/PacoVK/tapir/actions/workflows/build.yml/badge.svg)](https://github.com/PacoVK/tapir/actions/workflows/build.yml)
[![Release](https://github.com/PacoVK/tapir/actions/workflows/deploy.yml/badge.svg)](https://github.com/PacoVK/tapir/actions/workflows/deploy.yml)
[![Docs-deployment](https://github.com/PacoVK/tapir/actions/workflows/pages/pages-build-deployment/badge.svg)](https://github.com/PacoVK/tapir/actions/workflows/pages/pages-build-deployment)

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
  * Tapir integrates [Trivy](https://trivy.dev/) for that purpose
* It generates documentation and stats for the module 
  * See module dependencies, inputs, outputs and resources that will be generated
  * Tapir integrates [terraform-docs](https://terraform-docs.io/) for that purpose
* It provides several storage adapters
  * currently S3, AzureBlob and Local
* It provides several database adapters for the data
  * currently Dynamodb (default), Elasticsearch, CosmosDb
* It provides a REST-API for custom integrations and further automation
  Tapir is build on [Quarkus](https://quarkus.io/) and [ReactJS](https://reactjs.org/). You can run Tapir wherever you can run Docker images.
* If you run Tapir with local storage, it can even be operated in an **air-gaped** environment, with no internet access 

Apart from the above, [this is what Wikipedia knows about Tapirs](https://en.wikipedia.org/wiki/Tapir).

## Overview

### Deployment

**NOTE** starting with version `0.6.0` authentication is required. Hence, you need an OIDC IDP to run Tapir. 
Read more about the [authentication](./docs/configuration.md#authentication) below.

You can run Tapir wherever you can run Docker images.
Images are available on [DockerHub](https://hub.docker.com/r/pacovk/tapir) `pacovk/tapir` and [AWS Elastic Container Registry](https://gallery.ecr.aws/pacovk/tapir) `public.ecr.aws/pacovk/tapir`.
There are samples with Terraform in `examples/`.
* [AWS AppRunner](./examples/aws/apprunner)
* [AWS EKS](./examples/aws/eks)

### Configure

Tapir is configured via environment variables. You can learn how to set up Tapir [here](./docs/configuration.md).

### How-to

To see how to use Tapir, please read the [usage docs](./docs/usage.md).

## Troubleshoot

See [troubleshooting docs](./docs/TROUBLESHOOT.md)

## Roadmap

* Add more storage adapter
  * GCP
* Add more Database adapter
  * Postgresql
* Provide a Github/ Gitlab integration to crawl for additional code metrics and ownership informations

## Contribution

If you want to contribute to this project, please read the [contribution guidelines](./CONTRIBUTING.md).
A detailed How-to guide on local development can be found in the [docs](dev/docs/RUNBOOK.md).

**Actively searching** for contributors. <br/>
**Feedback** is always appreciated :rainbow: <br/>
Feel free to open an Issue (Bug- /Feature-Request)
or provide a Pull request. :wrench:

## Contributors ✨

Thanks go to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):
<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://pascal.euhus.dev/"><img src="https://avatars.githubusercontent.com/u/27785614?v=4?s=100" width="100px;" alt="PacoVK"/><br /><sub><b>PacoVK</b></sub></a><br /><a href="https://github.com/PacoVK/tapir/pulls?q=is%3Apr+reviewed-by%3APacoVK" title="Reviewed Pull Requests">👀</a> <a href="#projectManagement-PacoVK" title="Project Management">📆</a> <a href="#maintenance-PacoVK" title="Maintenance">🚧</a> <a href="#example-PacoVK" title="Examples">💡</a> <a href="https://github.com/PacoVK/tapir/commits?author=PacoVK" title="Code">💻</a> <a href="https://github.com/PacoVK/tapir/commits?author=PacoVK" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/andrea-defraia"><img src="https://avatars.githubusercontent.com/u/56583671?v=4?s=100" width="100px;" alt="Andrea Defraia"/><br /><sub><b>Andrea Defraia</b></sub></a><br /><a href="#example-andrea-defraia" title="Examples">💡</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/WeiMengXS"><img src="https://avatars.githubusercontent.com/u/54929266?v=4?s=100" width="100px;" alt="Wmxs"/><br /><sub><b>Wmxs</b></sub></a><br /><a href="https://github.com/PacoVK/tapir/issues?q=author%3AWeiMengXS" title="Bug reports">🐛</a> <a href="#ideas-WeiMengXS" title="Ideas, Planning, & Feedback">🤔</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->
