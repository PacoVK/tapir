FROM aquasec/trivy:0.46.0 as SECURITY_SCANNER

FROM registry.access.redhat.com/ubi8/openjdk-17:1.17-1.1696520325

COPY --from=SECURITY_SCANNER /usr/local/bin/trivy /usr/bin/

USER root

RUN microdnf install golang && go install github.com/terraform-docs/terraform-docs@v0.16.0  \
    && mv /home/jboss/go/bin/terraform-docs /usr/bin/  \
    && microdnf clean all \
    && mkdir -p /home/jboss/.cache/trivy \
    && chmod a+wr /home/jboss/.cache/trivy

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
