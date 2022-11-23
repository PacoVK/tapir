# USAGE
define USAGE
Usage: make [help | bootstrap | install | dev | backend | webapp | terraform | down | clean]
endef
export USAGE

certDir := dev/certs
webAppDir := src/main/webapp
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
	@sleep 5
	@aws s3 mb s3://tf-registry --endpoint-url=${localstackUrl} --region eu-central-1

backend:
	@docker compose up -d
	@mvn quarkus:dev

webapp:
	@yarn --cwd ${webAppDir} start

terraform:
	AWS_METADATA_URL=${localstackUrl} terraform -chdir=example init

down:
	@docker compose down

clean:
	@docker compose down -v
	@mvn clean
	@rm -rf example/.terraform