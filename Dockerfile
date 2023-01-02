FROM ubuntu

RUN apt update && apt install -y curl

RUN curl -s https://raw.githubusercontent.com/aquasecurity/tfsec/master/scripts/install_linux.sh | bash
COPY target/quarkus-app/quarkus-run.jar /tf/registry/tapir.jar
