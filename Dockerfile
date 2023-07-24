FROM aquasec/tfsec:v1.28 as TFSEC

FROM golang:1.20 as TFDOCS
RUN go install github.com/terraform-docs/terraform-docs@v0.16.0

FROM registry.access.redhat.com/ubi8/openjdk-17:1.16-2

COPY --from=TFSEC /usr/bin/tfsec /usr/bin/
COPY --from=TFDOCS /go/bin/terraform-docs /usr/bin/

# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --chown=185 target/quarkus-app/lib/ /tf/registry/lib/
COPY --chown=185 target/quarkus-app/quarkus-run.jar /tf/registry/tapir.jar
COPY --chown=185 target/quarkus-app/app/ /tf/registry/app/
COPY --chown=185 target/quarkus-app/quarkus/ /tf/registry/quarkus/

EXPOSE 8080

USER 185

ENV AB_JOLOKIA_OFF=""
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/tf/registry/tapir.jar"
