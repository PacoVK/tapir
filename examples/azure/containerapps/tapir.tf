# Logging
resource "azurerm_log_analytics_workspace" "la" {
  name                = "tapir"
  resource_group_name = var.resource_group_name
  location            = var.resource_group_location
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

# Create a container app env + container
resource "azurerm_container_app_environment" "env" {
  name                       = "tapir-env"
  resource_group_name        = var.resource_group_name
  location                   = var.resource_group_location
  log_analytics_workspace_id = azurerm_log_analytics_workspace.la.id
}

resource "azurerm_container_app" "container" {
  name                         = "tapir"
  container_app_environment_id = azurerm_container_app_environment.env.id
  resource_group_name          = var.resource_group_name
  revision_mode                = "Single"

  ingress {
    target_port      = 8080
    external_enabled = true

    traffic_weight {
      label           = "tapir"
      percentage      = 100
      latest_revision = true
    }
  }

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.kv_access.id]
  }

  secret {
    name                = var.auth_client_secret_vault_name
    key_vault_secret_id = azurerm_key_vault_secret.auth_client_secret.id
    identity            = azurerm_user_assigned_identity.kv_access.id
  }

  secret {
    name                = var.storage_account_key_vault_name
    key_vault_secret_id = azurerm_key_vault_secret.storage_key.id
    identity            = azurerm_user_assigned_identity.kv_access.id
  }

  template {
    container {
      name   = "tapir"
      image  = "pacovk/tapir:latest"
      cpu    = 2
      memory = "4Gi"

      env {
        name  = "BACKEND_CONFIG"
        value = "cosmosdb"
      }

      env {
        name        = "BACKEND_AZURE_MASTER_KEY"
        secret_name = var.storage_account_key_vault_name
      }

      env {
        name  = "BACKEND_AZURE_ENDPOINT"
        value = azurerm_cosmosdb_account.acc.endpoint
      }

      env {
        name  = "STORAGE_CONFIG"
        value = "azureBlob"
      }

      env {
        name  = "AZURE_BLOB_CONNECTION_STRING"
        value = azurerm_storage_account.sa.primary_connection_string
      }

      env {
        name  = "STORAGE_ACCESS_SESSION_DURATION"
        value = 60
      }

      # https://github.com/PacoVK/tapir/issues/379
      env {
        name  = "QUARKUS_OIDC_PROVIDER"
        value = "microsoft"
      }

      env {
        name  = "QUARKUS_OIDC_TOKEN_CUSTOMIZER_NAME"
        value = "azure-access-token-customizer"
      }

      #  Azure AD stores app roles in a claim called /roles, not Quarkus default of /groups
      env {
        name  = "QUARKUS_OIDC_ROLES_ROLE_CLAIM_PATH"
        value = "roles"
      }

      env {
        name  = "QUARKUS_OIDC_ROLES_SOURCE"
        value = "idtoken"
      }

      env {
        name  = "QUARKUS_OIDC_AUTHENTICATION_FORCE_REDIRECT_HTTPS_SCHEME"
        value = true
      }

      env {
        name  = "QUARKUS_HTTP_CORS_ORIGINS"
        value = "*" # Recommend injecting your URL here
      }

      env {
        name  = "AUTH_ENDPOINT"
        value = var.auth_endpoint
      }

      env {
        name  = "AUTH_CLIENT_ID"
        value = var.auth_client_id
      }

      env {
        name        = "AUTH_CLIENT_SECRET"
        secret_name = var.auth_client_secret_vault_name
      }

      env {
        name        = "REGISTRY_GPG_KEYS_0__ID"
        secret_name = var.gpg_id
      }

      env {
        name        = "REGISTRY_GPG_KEYS_0__ASCII_ARMOR"
        secret_name = var.gpg_armor
      }
    }
  }
}
