variable "subscription_id" {
  description = "The Azure subscription id to deploy all resources to."
  type = string
}

variable "auth_endpoint" {
  description = "Azure AD > Admin > App registrations > Your app > Directory (tenant) ID."
  type = string
}

variable "auth_client_id" {
  description = "Azure AD > Admin > App registrations > Your app > Application (client) ID."
  type = string
}

variable "auth_client_secret" {
  type = string
  description = "Azure AD > Admin > App registrations > Your app > Certificates & secrets > Client secrets > Value."
  sensitive = true
}

variable "auth_client_secret_vault_name" {
  default = "authclientsecret"
  description = "The Key Vault secret name for Auth Client secret."
  type = string
}

variable "storage_account_key_vault_name" {
  default = "storageaccountkey"
  description = "The Key Vault secret name for Service Account access key."
  type = string
}

variable "resource_group_name" {
  default = "tapir-rg"
  description = "The name for the resource group which will container all resources."
  type = string
}

variable "resource_group_location" {
  default = "uksouth"
  description = "Which Azure location to deploy to."
  type = string
}

variable "storage_account_name" {
  description = "The name of the storage account which will contain terraform modules/providers."
  type = string
}

variable "cosmos_db_account_name" {
  default = "tapir-cosmosdb-account"
  description = "The account name for the CosmosDB."
  type = string
}

variable "gpg_id" {
  description = "GPG key ID used to sign provider binaries."
  type = string
}

variable "gpg_armor" {
  description = "Armord GPG key which is also base64 encoded again. e.g: gpg --armor --export <KEY_ID> | base64"
  type = string
}

variable "vault_ip_list" {
  default = []
  description = "A list of IP Addresses which can access the deployed Key Vault."
  type = list(string)
}

variable "vault_current_user_object_id" {
  description = "The Azure Id of a group or user who should have portal permissions to manage the vault."
  type        = string
}