# Introduction 

Deploys [Tapir](https://github.com/PacoVK/tapir/) to [Azure Container Apps](https://azure.microsoft.com/en-us/products/container-apps) with supporting services.

## Services

The following services are deployed:

- **rg.tf** - The resource group to contain all resources.
- **db.tf** - CosmoDB and a SQL Container for Tapir.
- **sa.tf** - A Storage Account for hosted Terraform modules/providers.
- **vault.f** - Holds the various secrets Tapir needs when launching the container (SSO keys, SA keys, etc).
- **tapir.tf** - Creates a Container App for Tapir. 

## Azure SSO

Azure AD (or another provider) must be configured for Tapir before it will launch.

See `variables.tf` for `auth_endpoint`, `auth_client_id` and `auth_client_secret`.

The above are only present after creating an [App Registration](https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-register-app).

You will also need to:

1. Configure Redirect URIs to include:
   1. `https://<DOMAIN>.uksouth.azurecontainerapps.io/management`
   2. `https://<DOMAIN>.uksouth.azurecontainerapps.io/providers`
   3. `https://<DOMAIN>.uksouth.azurecontainerapps.io/`
2. Enable the `Id Tokens` flow type and only the current directory is enabled.
3. Create an [App Role](https://learn.microsoft.com/en-us/entra/identity-platform/howto-add-app-roles-in-apps) with a display name of `groups`, type of `Users/Groups` and a value of `admin`. 
4. Configure the token to include `openid`, `profile` and `email` scopes (aka API Permissions).
5. Configure the token to include `groups` optional claim.
6. Alter the admin Entra Id group to have the `admin` App role.
7. Create a client secret and store securely!
