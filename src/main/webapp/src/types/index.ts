export interface Module {
    namespace: string;
    name: string;
    provider: Provider;
}

export enum Provider {
    AWS = "aws",
    GOOGLE = "google",
    AZURE = "azurerm",
    KUBERNETES = "kubernetes"
}