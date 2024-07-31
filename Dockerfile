FROM aquasec/trivy:0.54.1 as SECURITY_SCANNER

FROM golang:alpine3.19 as TERRAFORM_DOCS

RUN go install github.com/terraform-docs/terraform-docs@v0.17.0

FROM amazoncorretto:21-alpine3.19-jdk

RUN mkdir -p /home/jboss/.cache/trivy \
    && chmod a+wr /home/jboss/.cache/trivy

RUN addgroup -g 187 tapir && adduser -D -u 187 -G tapir tapir

# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --chown=187 target/quarkus-app/lib/ /tf/registry/lib/
COPY --chown=187 target/quarkus-app/quarkus-run.jar /tf/registry/tapir.jar
COPY --chown=187 target/quarkus-app/app/ /tf/registry/app/
COPY --chown=187 target/quarkus-app/quarkus/ /tf/registry/quarkus/

USER tapir

COPY --from=TERRAFORM_DOCS /go/bin/terraform-docs /usr/bin/

COPY --from=SECURITY_SCANNER /usr/local/bin/trivy /usr/bin/

EXPOSE 8080

ENV AB_JOLOKIA_OFF=""
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/tf/registry/tapir.jar"

ENTRYPOINT ["java"]

CMD ["-jar", "/tf/registry/tapir.jar"]
