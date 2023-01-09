# USAGE
define USAGE
Usage: make [help | bootstrap | install | dev | backend | terraform | down | clean]
endef
export USAGE

certDir := dev/certs
webAppDir := src/main/webui
localstackUrl := http://localhost:4566

help:
	@echo "$$USAGE"

install:
	@yarn --cwd ${webAppDir} install

bootstrap:
	@mkdir -p ${certDir}
	@mkcert -install
	@mkcert -cert-file ${certDir}/cert.pem -key-file ${certDir}/key.pem 127.0.0.1

dev: bootstrap
	@docker compose up -d

backend:
	@docker compose up -d
	@mvn quarkus:dev

terraform:
	AWS_METADATA_URL=${localstackUrl} terraform -chdir=dev/example init

down:
	@docker compose down

clean:
	@docker compose down -v
	@mvn clean
	@rm -rf dev/example/.terraform