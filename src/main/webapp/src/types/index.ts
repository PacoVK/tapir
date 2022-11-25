export interface Module {
    namespace: string;
    name: string;
    provider: Provider;
    downloads: number;
    published_at: string
    versions: { version: string }[];
}

export enum Provider {
    AWS = "aws",
    GOOGLE = "google",
    AZURE = "azurerm",
    KUBERNETES = "kubernetes"
}