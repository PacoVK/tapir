export interface Module {
  id: string;
  namespace: string;
  name: string;
  provider: Provider;
  downloads: number;
  published_at: string;
  versions: { version: string }[];
}

export interface Misconfiguration {
  resource: string;
  severity: string;
  rule_description: string;
  description: string;
  impact: string;
  resolution: string;
  links: string;
  location: {
    filename: string;
    start_line: number;
    end_line: number;
  };
}
export enum Provider {
  AWS = "aws",
  GOOGLE = "google",
  AZURE = "azurerm",
  KUBERNETES = "kubernetes",
}

export enum Severity {
  UNKNOWN,
  LOW,
  MEDIUM,
  HIGH,
  CRITICAL,
}
