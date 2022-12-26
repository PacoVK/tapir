FROM ubuntu

RUN apt update && apt install -y curl

RUN curl -s https://raw.githubusercontent.com/aquasecurity/tfsec/master/scripts/install_linux.sh | bash

COPY target/private-terraform-registry-api-1.0.0-SNAPSHOT-runner /tf/registry

ENTRYPOINT /tf/registry