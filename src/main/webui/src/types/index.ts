export interface Module {
  namespace: string;
  name: string;
  provider: Provider;
  downloads: number;
  published_at: string;
  versions: { version: string }[];
}

export interface Misconfiguration {
  Title: string;
  Severity: string;
  Message: string;
  Resolution: string;
  PrimaryURL: string;
  CauseMetadata: {
    Resource: string;
    StartLine: number;
    EndLine: number;
  };
}

export enum Provider {
  AWS = "aws",
  GOOGLE = "google",
  AZURE = "azurerm",
  KUBERNETES = "kubernetes",
}
