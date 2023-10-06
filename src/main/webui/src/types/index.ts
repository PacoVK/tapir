export interface Module {
  id: string;
  namespace: string;
  name: string;
  provider: ProviderType;
  downloads: number;
  published_at: string;
  versions: { version: string }[];
}

export interface Provider {
  id: string;
  namespace: string;
  type: string;
  downloads: number;
  published_at: string;
  versions: { version: string }[];
}

export type DeployKey = {
  id: string;
  key: string;
  lastModifiedAt: string;
};

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
export enum ProviderType {
  AWS = "aws",
  GOOGLE = "google",
  AZURE = "azurerm",
  KUBERNETES = "kubernetes",
}

export interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

export interface ModuleInputProps {
  inputs: {
    name: string;
    description: string;
    type: string;
    required: boolean;
  }[];
}

export interface ModuleOutputProps {
  outputs: {
    name: string;
    description?: string;
  }[];
}

export interface ModuleResourcesProps {
  resources: {
    name: string;
    type: string;
  }[];
}

export interface ReportFindingsProps {
  reports: { [p: string]: unknown } | ArrayLike<unknown>;
}

export interface ModuleDependenciesProps {
  modules: {
    source: string;
    version: string;
  }[];

  providers: {
    name: string;
    alias?: string;
    version?: string;
  }[];
}

export interface ModuleAnalysisTabProps {
  version: string;
  reports: any;
  documentation: any;
}

export type User = {
  name: string;
  roles: string[];
  givenName?: string;
  familyName?: string;
  email?: string;
  preferredUsername?: string;
}
