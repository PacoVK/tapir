# Tapir Chart

A Helm chart for Tapir, a private Terraform registry.

Source code can be found here:

* <https://github.com/PacoVK/tapir>

## Prerequisites

- Kubernetes: `>=1.23.0-0`
- Helm v3.0.0+

## Installing the Chart

To install the chart with the release name `my-release`:

```console
$ git clone https://github.com/PacoVK/tapir.git

$ helm install my-release charts/
NAME: my-release
...
```

## Changelog

## General parameters

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| autoscaling.behavior | object | `{}` | Configures scaling behavior of the target in both Up and Down directions |
| autoscaling.enabled | bool | `false` | Enable Horizontal Pod Autoscaler ([HPA]) for the `tapir` deployment |
| autoscaling.maxReplicas | int | `1` | Maximum number of replicas for the `tapir` deployment [HPA] |
| autoscaling.metrics | object | `{}` | Configures custom HPA metrics for the `tapir` deployment Ref: https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/ |
| autoscaling.minReplicas | int | `1` | Minimum number of replicas for the `tapir` deployment [HPA] |
| autoscaling.targetCPUUtilizationPercentage | string | `""` | Average CPU utilization percentage for the `tapir` deployment [HPA] |
| autoscaling.targetMemoryUtilizationPercentage | string | `""` | Average memory utilization percentage for the `tapir` deployment [HPA] |
| certificate.additionalHosts | list | `[]` | Certificate Subject Alternate Names (SANs) |
| certificate.annotations | object | `{}` | Annotations to be applied to the `tapir` Certificate |
| certificate.domain | string | `"tapir.example.com"` | Certificate primary domain (CommonName) |
| certificate.duration | string | `""` (defaults to 2160h = 90d if not specified) | The requested 'duration' (i.e. lifetime) of the certificate. |
| certificate.enabled | bool | `false` | Deploy a Certificate resource (requires cert-manager) |
| certificate.issuer.group | string | `""` | Certificate issuer group. Set if using an external issuer. |
| certificate.issuer.kind | string | `""` | Certificate issuer kind. Either `Issuer` or `ClusterIssuer` |
| certificate.issuer.name | string | `""` | Certificate issuer name. Eg. `letsencrypt` |
| certificate.privateKey | object | `{"algorithm":"RSA","encoding":"PKCS1","rotationPolicy":"Never","size":2048}` | Private key of the certificate |
| certificate.privateKey.algorithm | string | `"RSA"` | Algorithm used to generate certificate private key. One of: `RSA`, `Ed25519` or `ECDSA` |
| certificate.privateKey.encoding | string | `"PKCS1"` | The private key cryptography standards (PKCS) encoding for private key. Either: `PCKS1` or `PKCS8` |
| certificate.privateKey.rotationPolicy | string | `"Never"` | Rotation policy of private key when certificate is re-issued. Either: `Never` or `Always` |
| certificate.privateKey.size | int | `2048` | Key bit size of the private key. If algorithm is set to `Ed25519`, size is ignored. |
| certificate.renewBefore | string | `""` (defaults to 360h = 15d if not specified) | How long before the expiry a certificate should be renewed. |
| certificate.secretName | string | `"tapir-tls"` | The name of the Secret that will be created and managed by this Certificate resource |
| certificate.usages | list | `[]` | Usages for the certificate |
| createClusterRoles | bool | `true` | Create roles for cluster-wide installation |
| deployment.affinity | object | `{}` | Assign custom affinity rules to the deployment |
| deployment.annotations | object | `{}` | Annotations to be added to `tapir` deployment |
| deployment.configuration | object | `{"apiMaxBodySize":"100M","auth":{"attribute":{"email":"email","familyName":"family_name","givenName":"given_name","prefUsername":"preferred_username"},"clientID":"","clientSecret":"","endSessionPath":"/protocol/openid-connect/logout","endpoint":"","path":"","roleSource":"accesstoken","tokenPath":""},"backend":{"cosmosDB":{"endpoint":"","masterKey":""},"elasticsearch":{"host":""},"type":"dynamodb"},"gpg":{"keyArmor":"","keyID":""},"storage":{"azureBlob":{"bucketName":"tf-registry","connectionString":""},"localRegistry":{"hostname":"localhost","port":443},"s3":{"bucketName":"tf-registry","bucketRegion":"eu-central-1"},"storageAccessDuration":5,"type":"s3"}}` | Deployment's configuration, populates all the required environmental variables |
| deployment.configuration.apiMaxBodySize | string | `"100M"` | The maximum payload size for module/providers to be uploaded |
| deployment.configuration.auth | object | `{"attribute":{"email":"email","familyName":"family_name","givenName":"given_name","prefUsername":"preferred_username"},"clientID":"","clientSecret":"","endSessionPath":"/protocol/openid-connect/logout","endpoint":"","path":"","roleSource":"accesstoken","tokenPath":""}` | OpenID Connect (OIDC) configuration |
| deployment.configuration.auth.attribute | object | `{"email":"email","familyName":"family_name","givenName":"given_name","prefUsername":"preferred_username"}` | OIDC attribute names |
| deployment.configuration.auth.attribute.email | string | `"email"` | The attribute name in the token where the email is placed in |
| deployment.configuration.auth.attribute.familyName | string | `"family_name"` | The attribute name in the token where the family name is placed in |
| deployment.configuration.auth.attribute.givenName | string | `"given_name"` | The attribute name in the token where the given name is placed in |
| deployment.configuration.auth.attribute.prefUsername | string | `"preferred_username"` | The attribute name in the token where the preferred username is placed in |
| deployment.configuration.auth.clientID | string | `""` | Client ID |
| deployment.configuration.auth.clientSecret | string | `""` | Client secret if required by client |
| deployment.configuration.auth.endSessionPath | string | `"/protocol/openid-connect/logout"` | IDP end session path, which will be used to logout |
| deployment.configuration.auth.endpoint | string | `""` | The base URL of the OIDC server |
| deployment.configuration.auth.path | string | `""` | Relative path or absolute URL of the OIDC authorization endpoint |
| deployment.configuration.auth.roleSource | string | `"accesstoken"` | The source of the role claim in the access token |
| deployment.configuration.auth.tokenPath | string | `""` | Relative path or absolute URL of the OIDC token endpoint which issues access and refresh tokens |
| deployment.configuration.backend | object | `{"cosmosDB":{"endpoint":"","masterKey":""},"elasticsearch":{"host":""},"type":"dynamodb"}` | Database backend configuration |
| deployment.configuration.backend.cosmosDB | object | `{"endpoint":"","masterKey":""}` | CosmosDB backend configuration |
| deployment.configuration.backend.cosmosDB.endpoint | string | `""` | CosmosDB endpoint |
| deployment.configuration.backend.cosmosDB.masterKey | string | `""` | CosmosDB master key |
| deployment.configuration.backend.elasticsearch | object | `{"host":""}` | Elasticsearch backend configuration |
| deployment.configuration.backend.elasticsearch.host | string | `""` | Elasticsearch host |
| deployment.configuration.backend.type | string | `"dynamodb"` | One of: elasticsearch,dynamodb,cosmosdb |
| deployment.configuration.gpg | object | `{"keyArmor":"","keyID":""}` | GPG configuration |
| deployment.configuration.gpg.keyArmor | string | `""` | Ascii armored and bas64 encoded GPG public key (only RSA/DSA supported) |
| deployment.configuration.gpg.keyID | string | `""` | GPG key ID of the key to be used (eg. D17C807B4156558133A1FB843C7461473EB779BD) |
| deployment.configuration.storage | object | `{"azureBlob":{"bucketName":"tf-registry","connectionString":""},"localRegistry":{"hostname":"localhost","port":443},"s3":{"bucketName":"tf-registry","bucketRegion":"eu-central-1"},"storageAccessDuration":5,"type":"s3"}` | Storage configuration |
| deployment.configuration.storage.azureBlob | object | `{"bucketName":"tf-registry","connectionString":""}` | Azure Blob storage configuration |
| deployment.configuration.storage.azureBlob.bucketName | string | `"tf-registry"` | Azure Blob bucket name |
| deployment.configuration.storage.azureBlob.connectionString | string | `""` | Azure Blob connection string |
| deployment.configuration.storage.localRegistry | object | `{"hostname":"localhost","port":443}` | Tapir's local storage configuration |
| deployment.configuration.storage.localRegistry.hostname | string | `"localhost"` | Tapir's DNS record |
| deployment.configuration.storage.localRegistry.port | int | `443` | Tapir's external port |
| deployment.configuration.storage.s3 | object | `{"bucketName":"tf-registry","bucketRegion":"eu-central-1"}` | S3 storage configuration |
| deployment.configuration.storage.s3.bucketName | string | `"tf-registry"` | S3 bucket name |
| deployment.configuration.storage.s3.bucketRegion | string | `"eu-central-1"` | S3 bucket region |
| deployment.configuration.storage.storageAccessDuration | int | `5` | Amount of minutes the signed download url is valid |
| deployment.configuration.storage.type | string | `"s3"` | One of: "s3,azureBlob,local" |
| deployment.containerPort | int | `8080` | Tapir container port |
| deployment.containerSecurityContext | object | `{}` | Container level security context |
| deployment.dnsConfig | object | `{}` | [DNS configuration] |
| deployment.dnsPolicy | string | `"ClusterFirst"` | Alternative DNS policy |
| deployment.envFrom | object | `{}` | envFrom to pass to the `tapir` pods |
| deployment.extraEnv | object | `{}` | Environment variables to pass to the `tapir` pods, other than ones defined in deployment.configuration object |
| deployment.hostAliases | list | `[]` | Additional entries that will be injected in the pod's /etc/hosts file |
| deployment.hostNetwork | bool | `false` | Host network for `tapir` pods |
| deployment.image | object | `{"imagePullPolicy":"Always","repository":"pacovk/tapir","tag":""}` | Default image used by `tapir` deployment |
| deployment.image.imagePullPolicy | string | `"Always"` | Image pull policy for tapi |
| deployment.image.repository | string | `"pacovk/tapir"` | Repository to use for tapir |
| deployment.image.tag | string | `""` | Tag to use for tapir |
| deployment.imagePullSecrets | list | `[]` | Secrets with credentials to pull images from a private registry |
| deployment.labels | object | `{}` | Labels to be added to `tapir` deployment |
| deployment.lifecycle | object | `{}` | Specify postStart and preStop lifecycle hooks for your `tapir` container |
| deployment.livenessProbe | object | `{}` | Readiness and liveness probes for `tapir` |
| deployment.nodeSelector | object | `{}` | [Node selector] |
| deployment.podAnnotations | object | `{}` | Annotations to be added to all deployed pods |
| deployment.podLabels | object | `{}` | Labels  to be added to all deployed pods |
| deployment.priorityClassName | string | `""` | Priority class for the tapir pods |
| deployment.readinessProbe | object | `{}` |  |
| deployment.replicas | int | `1` | The number of application pods to run |
| deployment.resources | object | `{}` | Resource limits and requests for the `tapir`` pods |
| deployment.revisionHistoryLimit | int | `3` | Number of old deployment ReplicaSets to retain. The rest will be garbage collected. |
| deployment.securityContext | object | `{}` | Pod level security context |
| deployment.strategy | object | `{}` | Deployment strategy for the deployment |
| deployment.terminationGracePeriodSeconds | int | `30` | terminationGracePeriodSeconds for container lifecycle hook |
| deployment.tolerations | list | `[]` | [Tolerations] |
| deployment.topologySpreadConstraints | list | `[]` | Assign custom topologySpreadConstraints rules to the `tapir` pods |
| fullnameOverride | string | `""` | String to fully override `"tapir.fullname` |
| ingress.annotations | object | `{}` | Additional ingress annotations |
| ingress.enabled | bool | `false` | Enable an ingress resource for the `tapir` registry |
| ingress.extraPaths | list | `[]` | Additional ingress paths |
| ingress.hosts | list | `[]` | List of ingress hosts |
| ingress.ingressClassName | string | `""` | Defines which ingress controller will implement the resource |
| ingress.labels | object | `{}` | Additional ingress labels |
| ingress.pathType | string | `"ImplementationSpecific"` | Ingress path type. One of `Exact`, `Prefix` or `ImplementationSpecific` |
| ingress.paths | list | `["/*"]` | List of ingress paths |
| ingress.tls | list | `[]` | Ingress TLS configuration |
| nameOverride | string | `""` | Provide a name in place of `tapir` |
| pdb.annotations | object | `{}` | Annotations to be added to `tapir` pdb |
| pdb.enabled | bool | `false` | Deploy a [PodDistruptionBudget] for the `tapir` deployment |
| pdb.labels | object | `{}` |  |
| pdb.maxUnavailable | string | `""` | Number of pods that are unavailable after eviction as number or percentage (eg.: 50%). |
| pdb.minAvailable | string | `""` (defaults to 0 if not specified) | Number of pods that are available after eviction as number or percentage (eg.: 50%) |
| service.annotations | object | `{}` | Service annotations |
| service.externalIPs | list | `[]` | Service external IPs |
| service.externalTrafficPolicy | string | `""` | Denotes if the service desires to route external traffic to node-local or cluster-wide endpoints |
| service.labels | object | `{}` | Service labels |
| service.loadBalancerIP | string | `""` | LoadBalancer will be created with the IP specified in this field |
| service.loadBalancerSourceRanges | list | `[]` | Source IP ranges to allow access to service from |
| service.nodePortHttps | int | `30443` | Service https port for NodePort service type (only if `service.type` is set to "NodePort") |
| service.portHttps | int | `443` | Service https port |
| service.sessionAffinity | string | `""` | Used to maintain session affinity. Supports `ClientIP` and `None` |
| service.type | string | `"ClusterIP"` | Service type |
| serviceAccount.annotations | object | `{}` | Annotations applied to created service account |
| serviceAccount.automountServiceAccountToken | bool | `false` | Automount API credentials for the service account |
| serviceAccount.create | bool | `true` | Create a service account for `tapir` deployment |
| serviceAccount.labels | object | `{}` | Labels applied to created service account |
| serviceAccount.name | string | `"tapir"` | Service account name |
