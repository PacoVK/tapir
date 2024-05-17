# Storage account for storing modules
resource "azurerm_storage_account" "sa" {
  name                     = var.storage_account_name
  resource_group_name      = var.resource_group_name
  location                 = var.resource_group_location
  account_tier             = "Standard"
  account_replication_type = "GRS"
}

resource "azurerm_storage_container" "container" {
  name                  = "tf-registry"
  storage_account_name  = azurerm_storage_account.sa.name
  container_access_type = "private"
}