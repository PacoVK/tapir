{{/*
Generate Tapir configuration with environment variables.
*/}}
{{- define "tapir.env" -}}
{{- $config := .Values.deployment.configuration -}}
{{- with $config.backend.type }}
- name: BACKEND_CONFIG
  value: {{ default "dynamodb" . }}
{{- end }}
{{- if (eq $config.backend.type "elasticsearch") }}
{{- with $config.backend.elasticsearch.host }}
- name: BACKEND_ELASTICSEARCH_HOST
  value: {{ . }}
{{- end }}
{{- end }}
{{- if (eq $config.backend.type "cosmosdb") }}
{{- with $config.backend.cosmosDB.endpoint }}
- name: BACKEND_AZURE_ENDPOINT
  value: {{ . }}
{{- end }}
{{- with $config.backend.cosmosDB.masterKey }}
- name: BACKEND_AZURE_MASTER_KEY
  value: {{ . }}
{{- end }}
{{- end }}
{{- with $config.storage.type }}
- name: STORAGE_CONFIG
  value: {{ default "s3" . }}
{{- end }}
{{- with $config.storageAccessDuration }}
- name: STORAGE_ACCESS_SESSION_DURATION
  value: {{ default 5 . }}
{{- end }}
{{- if (eq $config.storage.type "s3") }}
{{- with $config.storage.s3.bucketName }}
- name: S3_STORAGE_BUCKET_NAME
  value: {{ default "tf-registry" . }}
{{- end }}
{{- with $config.storage.s3.bucketRegion }}
- name: S3_STORAGE_BUCKET_REGION
  value: {{ default "eu-central-1" . }}
{{- end }}
{{- end }}
{{- if (eq $config.storage.type "azureBlob") }}
{{- with $config.storage.azureBlob.connectionString }}
- name: AZURE_BLOB_CONNECTION_STRING
  value: {{ . }}
{{- end }}
{{- with $config.storage.azureBlob.bucketName }}
- name: AZURE_BLOB_CONTAINER_NAME
  value: {{ default "tf-registry" . }}
{{- end }}
{{- end }}
{{- if (eq $config.storage.type "local") }}
{{- with $config.storage.localRegistry.hostname }}
- name: REGISTRY_HOSTNAME
  value: {{ default "localhost" . }}
{{- end }}
{{- with $config.storage.localRegistry.port }}
- name: REGISTRY_PORT
  value: {{ default 443 . }}
{{- end }}
{{- end }}
{{- with $config.apiMaxBodySize }}
- name: API_MAX_BODY_SIZE
  value: {{ default "100Mi" . }}
{{- end }}
{{- with $config.gpg.keyID }}
- name: REGISTRY_GPG_KEYS_0__ID
  value: {{ . }}
{{- end }}
{{- with $config.gpg.keyArmor }}
- name: REGISTRY_GPG_KEYS_0__ASCII_ARMOR
  value: {{ . }}
{{- end }}
{{- with $config.auth.endpoint }}
- name: AUTH_ENDPOINT
  value: {{ . }}
{{- end }}
{{- with $config.auth.clientID }}
- name: AUTH_CLIENT_ID
  value: {{ . }}
{{- end }}
{{- with $config.auth.clientSecret }}
- name: AUTH_CLIENT_SECRET
  value: {{ . }}
{{- end }}
{{- with $config.auth.tokenPath }}
- name: AUTH_TOKEN_PATH
  value: {{ . }}
{{- end }}
{{- with $config.auth.path }}
- name: AUTH_PATH
  value: {{ . }}
{{- end }}
{{- with $config.auth.roleSource }}
- name: AUTH_ROLE_SOURCE
  value: {{ default "accesstoken" . }}
{{- end }}
{{- with $config.auth.attribute.email }}
- name: AUTH_TOKEN_ATTRIBUTE_EMAIL
  value: {{ default "email" . }}
{{- end }}
{{- with $config.auth.attribute.givenName }}
- name: AUTH_TOKEN_ATTRIBUTE_GIVEN_NAME
  value: {{ default "given_name" . }}
{{- end }}
{{- with $config.auth.attribute.familyName }}
- name: AUTH_TOKEN_ATTRIBUTE_FAMILY_NAME
  value: {{ default "family_name" . }}
{{- end }}
{{- with $config.auth.attribute.prefUsername }}
- name: AUTH_TOKEN_ATTRIBUTE_PREFERRED_USERNAME
  value: {{ default "preferred_username" . }}
{{- end }}
{{- with $config.auth.endSessionPath }}
- name: END_SESSION_PATH
  value: {{ default "/protocol/openid-connect/logout" . }}
{{- end }}
{{- end }}