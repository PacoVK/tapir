# Create a KV to hold various secrets that Tapir requires (i.e. SSO, Storage keys, etc).
resource "random_string" "keyvault_name" {
  length  = 13
  lower   = true
  special = false
}

resource "azurerm_key_vault" "kv" {
  name                        = random_string.keyvault_name.result
  resource_group_name         = var.resource_group_name
  location                    = var.resource_group_location
  enabled_for_disk_encryption = true
  tenant_id                   = data.azurerm_client_config.current.tenant_id
  soft_delete_retention_days  = 7
  purge_protection_enabled    = false

  sku_name = "standard"

  network_acls {
    bypass         = "AzureServices"
    default_action = "Deny"
    ip_rules       = concat(var.vault_ip_list, [azurerm_container_app_environment.env.static_ip_address])
  }
}

# Create managed identity to access KV when deploying container
resource "azurerm_user_assigned_identity" "kv_access" {
  resource_group_name = var.resource_group_name
  location            = var.resource_group_location
  name                = "sp-tapir-kv-access"
}

resource "azurerm_key_vault_access_policy" "sp" {
  key_vault_id = azurerm_key_vault.kv.id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = azurerm_user_assigned_identity.kv_access.principal_id

  secret_permissions = [
    "Get",
    "List"
  ]
}

resource "azurerm_key_vault_access_policy" "current_user" {
  key_vault_id = azurerm_key_vault.kv.id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = var.vault_current_user_object_id

  secret_permissions = [
    "Set",
    "Get",
    "List",
    "Delete",
    "Purge",
    "Recover"
  ]
}

# Add deployment secrets to KV to retrieve when deploying.
resource "azurerm_key_vault_secret" "auth_client_secret" {
  name         = var.auth_client_secret_vault_name
  value        = var.auth_client_secret
  key_vault_id = azurerm_key_vault.kv.id
}

resource "azurerm_key_vault_secret" "storage_key" {
  name         = var.storage_account_key_vault_name
  value        = azurerm_cosmosdb_account.acc.primary_key
  key_vault_id = azurerm_key_vault.kv.id
}
