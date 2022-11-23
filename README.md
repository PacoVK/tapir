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

