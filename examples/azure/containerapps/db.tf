# CosmosDB Account
resource "azurerm_cosmosdb_account" "acc" {
  name                          = var.cosmos_db_account_name
  location                      = var.resource_group_location
  resource_group_name           = var.resource_group_name
  public_network_access_enabled = false
  offer_type                    = "Standard"
  kind                          = "GlobalDocumentDB"

  consistency_policy {
    consistency_level = "Session"
  }

  geo_location {
    location          = var.resource_group_location
    failover_priority = 0
  }
}

# CosmosDB Database
resource "azurerm_cosmosdb_sql_database" "db" {
  name                = "tapir"
  resource_group_name = var.resource_group_name
  account_name        = azurerm_cosmosdb_account.acc.name
}