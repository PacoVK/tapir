# Runbook for local development 

This project uses [Quarkus](https://quarkus.io/), the Supersonic Subatomic Java Framework
and [ReactJS](https://reactjs.org/).

## Development

This project requires at least:

* Java 17 + Maven
* NodeJS 18+
* Docker 20+
* [Yarn](https://yarnpkg.com/getting-started/install)

### Setup

#### With Homebrew

Use the [Brewfile](./Brewfile) for all necessary dependencies:

```sh
brew bundle && make bootstrap
```

#### Linux - without Homebrew

Ensure you have the following tools installed:

* [mkcert](https://github.com/FiloSottile/mkcert)
    * [nss](https://man7.org/linux/man-pages/man5/nss.5.html) (if you use FireFox)
* [awscli](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
* [terraform](https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli)

### Start local dev environment

There is a [Makefile](../Makefile) to ease the initial setup.

1. Make sure you have the very base setup if you run this the first time.
```shell
make dev
```
2. Start the backend
```shell
make backend
```
> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

3. Test using the example Terraform code in `example`. You need to upload a module with the identifiers that are 
specified within the code.  
```shell
make terraform
```

### Clean everything
This will ensure a clean state, where you need to run `bootstrap` again. 
```shell
make clean
```

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

## Creating a native executable

You can create a native executable using:
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/private-core.terraform-registry-api-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- Elasticsearch REST client ([guide](https://quarkus.io/guides/elasticsearch)): Connect to an Elasticsearch cluster using the REST low level client
- Quarkus Quinoa ([docs](https://quarkiverse.github.io/quarkiverse-docs/quarkus-quinoa/dev/)): Quinoa is a Quarkus extension which eases the development, the build and serving single page apps or web components
- Quarkus AWS Services ([docs](https://quarkiverse.github.io/quarkiverse-docs/quarkus-amazon-services/dev/index.html)): Quarkus implementation of the AWS Java SDK
- Azurite Storage ([docs](https://learn.microsoft.com/en-us/azure/storage/common/storage-use-azurite)): Azure Blob Storage emulator