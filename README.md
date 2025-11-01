# Tapir
<p>  
  <a href="https://github.com/PacoVK/tapir?tab=readme-ov-file#contributors-">
    <img alt="Contributors" src="https://img.shields.io/github/all-contributors/pacovk/tapir">
  </a>
  <a href="https://hub.docker.com/r/pacovk/tapir">
    <img alt="Docker Pulls" src="https://img.shields.io/docker/pulls/pacovk/tapir">
  </a>
</p>
  
### A Private Terraform Registry

[![Test](https://github.com/PacoVK/tapir/actions/workflows/build.yml/badge.svg)](https://github.com/PacoVK/tapir/actions/workflows/build.yml)
[![Release](https://github.com/PacoVK/tapir/actions/workflows/deploy.yml/badge.svg)](https://github.com/PacoVK/tapir/actions/workflows/deploy.yml)
[![Docs-deployment](https://github.com/PacoVK/tapir/actions/workflows/pages/pages-build-deployment/badge.svg)](https://github.com/PacoVK/tapir/actions/workflows/pages/pages-build-deployment)
[<img src="https://api.gitsponsors.com/api/badge/img?id=569890667" height="20">](https://api.gitsponsors.com/api/badge/link?p=Fhcl2QYOz9lq0noRGHOc9bUnaferItDiM0xElJWfcZ5IlK5OfxhJLKK+G6b3G5zETsIBgzbNnEMKUsAJTf2TFg==)

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
* [Azure Containers](./examples/azure/containerapps)

Other deployment options available are:
* [Helm Chart](https://github.com/PacoVK/tapir-helm)

### Configure

Tapir is configured via environment variables. You can learn how to set up Tapir [here](./docs/configuration.md).

#### Health Checks

Tapir exposes health check endpoints for monitoring and orchestration:

**Endpoints:**
- `/q/health` - Combined health status (all checks)
- `/q/health/live` - Liveness probe (basic application is running)
- `/q/health/ready` - Readiness probe (application + dependencies ready)

### How-to

To see how to use Tapir, please read the [usage docs](./docs/usage.md).

## Troubleshoot

See [troubleshooting docs](./docs/TROUBLESHOOT.md)

## Publications

* [From Keycloak to Cognito: Building a Self-Hosted Terraform Registry on AWS](https://infrahouse.com/blog/2025-10-26-building-terraform-aws-registry/) - Blogpost about set up hosting Tapir on ECS with AWS Cognito - by [InfraHouse](https://infrahouse.com/) 

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
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/jonasz-lasut"><img src="https://avatars.githubusercontent.com/u/93281932?v=4?s=100" width="100px;" alt="Jonasz Łasut-Balcerzak"/><br /><sub><b>Jonasz Łasut-Balcerzak</b></sub></a><br /><a href="#example-jonasz-lasut" title="Examples">💡</a> <a href="https://github.com/PacoVK/tapir/commits?author=jonasz-lasut" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/tlchaffi"><img src="https://avatars.githubusercontent.com/u/128724533?v=4?s=100" width="100px;" alt="Tim Chaffin"/><br /><sub><b>Tim Chaffin</b></sub></a><br /><a href="https://github.com/PacoVK/tapir/pulls?q=is%3Apr+reviewed-by%3Atlchaffi" title="Reviewed Pull Requests">👀</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/tim-chaffin"><img src="https://avatars.githubusercontent.com/u/128724533?v=4?s=100" width="100px;" alt="Tim Chaffin"/><br /><sub><b>Tim Chaffin</b></sub></a><br /><a href="https://github.com/PacoVK/tapir/commits?author=tim-chaffin" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/TomBeckett"><img src="https://avatars.githubusercontent.com/u/10406453?v=4?s=100" width="100px;" alt="Tom Beckett"/><br /><sub><b>Tom Beckett</b></sub></a><br /><a href="#example-TomBeckett" title="Examples">💡</a> <a href="https://github.com/PacoVK/tapir/commits?author=TomBeckett" title="Code">💻</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/akuzminsky"><img src="https://avatars.githubusercontent.com/u/1763754?v=4?s=100" width="100px;" alt="Oleksandr Kuzminskyi"/><br /><sub><b>Oleksandr Kuzminskyi</b></sub></a><br /><a href="https://github.com/PacoVK/tapir/issues?q=author%3Aakuzminsky" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/GrzegorzHejman"><img src="https://avatars.githubusercontent.com/u/55591451?v=4?s=100" width="100px;" alt="GrzegorzHejman"/><br /><sub><b>GrzegorzHejman</b></sub></a><br /><a href="https://github.com/PacoVK/tapir/issues?q=author%3AGrzegorzHejman" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://hybridbrothers.com"><img src="https://avatars.githubusercontent.com/u/26713978?v=4?s=100" width="100px;" alt="Cédric Braekevelt"/><br /><sub><b>Cédric Braekevelt</b></sub></a><br /><a href="https://github.com/PacoVK/tapir/issues?q=author%3Acedricbraekevelt" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://lois.postu.la"><img src="https://avatars.githubusercontent.com/u/1423612?v=4?s=100" width="100px;" alt="Loïs Postula"/><br /><sub><b>Loïs Postula</b></sub></a><br /><a href="https://github.com/PacoVK/tapir/commits?author=loispostula" title="Code">💻</a> <a href="https://github.com/PacoVK/tapir/commits?author=loispostula" title="Documentation">📖</a> <a href="#ideas-loispostula" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/techchicken"><img src="https://avatars.githubusercontent.com/u/192054335?v=4?s=100" width="100px;" alt="techchicken"/><br /><sub><b>techchicken</b></sub></a><br /><a href="https://github.com/PacoVK/tapir/commits?author=techchicken" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://tech.margula.pl/"><img src="https://avatars.githubusercontent.com/u/3201201?v=4?s=100" width="100px;" alt="Michał Margula"/><br /><sub><b>Michał Margula</b></sub></a><br /><a href="https://github.com/PacoVK/tapir/commits?author=alchemyx" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/kimxogus"><img src="https://avatars.githubusercontent.com/u/11684628?v=4?s=100" width="100px;" alt="Taehyun Kim"/><br /><sub><b>Taehyun Kim</b></sub></a><br /><a href="#ideas-kimxogus" title="Ideas, Planning, & Feedback">🤔</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->
