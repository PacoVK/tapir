# private-core.terraform-registry-api Project

This project uses [Quarkus](https://quarkus.io/), the Supersonic Subatomic Java Framework.

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

### Starting components

You can run your application in dev mode that enables live coding using:
```shell script
make 
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

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
