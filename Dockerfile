FROM aquasec/trivy:0.45.1 as SECURITY_SCANNER

FROM golang:1.21 as TFDOCS
RUN go install github.com/terraform-docs/terraform-docs@v0.16.0

FROM registry.access.redhat.com/ubi8/openjdk-17:1.17-1.1693366272

COPY --from=SECURITY_SCANNER /usr/local/bin/trivy /usr/bin/
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
