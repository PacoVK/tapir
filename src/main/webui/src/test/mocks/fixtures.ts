import {
  DeployKey,
  Misconfiguration,
  Module,
  Provider,
  ProviderType,
  User,
} from "../../types";

export const createModule = (overrides?: Partial<Module>): Module => ({
  id: "foo-bar-aws-1.0.0",
  namespace: "foo",
  name: "bar",
  provider: ProviderType.AWS,
  downloads: 42,
  published_at: "2024-01-15T10:30:00Z",
  versions: [{ version: "1.0.0" }, { version: "0.9.0" }],
  ...overrides,
});

export const createProvider = (overrides?: Partial<Provider>): Provider => ({
  id: "hashicorp-aws",
  namespace: "hashicorp",
  type: "aws",
  downloads: 100,
  published_at: "2024-02-20T14:00:00Z",
  versions: [{ version: "5.0.0" }, { version: "4.9.0" }],
  ...overrides,
});

// Provider with versions as object map (runtime shape used by ProviderElement/ProviderDetails)
export const createProviderWithVersionMap = (
  overrides?: Record<string, unknown>,
) => ({
  id: "hashicorp-aws",
  namespace: "hashicorp",
  type: "aws",
  downloads: 100,
  published_at: "2024-02-20T14:00:00Z",
  versions: { "5.0.0": {}, "4.9.0": {} } as Record<string, unknown>,
  ...overrides,
});

export const createDeployKey = (overrides?: Partial<DeployKey>): DeployKey => ({
  id: "dk-test-key-001",
  key: "sdlshdfkjlf-fake-key-value",
  lastModifiedAt: "2024-03-10T08:15:00Z",
  ...overrides,
});

export const createMisconfiguration = (
  overrides?: Partial<Misconfiguration>,
): Misconfiguration => ({
  resource: "aws_security_group.no_issue",
  severity: "CRITICAL",
  rule_description: "An ingress security group rule allows traffic from /0.",
  description: "Security group rule allows ingress from public internet.",
  impact: "Your port exposed to the internet",
  resolution: "Set a more restrictive cidr range",
  links: [
    "https://aquasecurity.github.io/tfsec/v1.28.1/checks/aws/ec2/no-public-ingress-sgr/",
    "https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule#cidr_blocks",
  ] as unknown as string,
  location: {
    filename: "/foo/bar/damaged.tf",
    start_line: 31,
    end_line: 40,
  },
  ...overrides,
});

export const createUser = (overrides?: Partial<User>): User => ({
  name: "Test User",
  roles: [],
  givenName: "Test",
  familyName: "User",
  email: "test@example.com",
  preferredUsername: "testuser",
  ...overrides,
});

export const createAdminUser = (overrides?: Partial<User>): User =>
  createUser({ roles: ["admin"], ...overrides });

export const createDocumentation = (overrides?: Record<string, unknown>) => ({
  inputs: [
    {
      name: "repositories",
      type: "map(object({}))",
      description: "List of repositories to create",
      required: true,
    },
  ],
  modules: [
    {
      name: "vote_service_sg",
      source: "terraform-aws-modules/security-group/aws",
      version: "",
    },
  ],
  outputs: [{ name: "github_roles" }, { name: "registries" }],
  providers: [{ name: "aws" }],
  resources: [
    {
      name: "this",
      type: "ecr_lifecycle_policy",
      source: "hashicorp/aws",
      mode: "managed",
      version: "latest",
    },
  ],
  ...overrides,
});

export const createSearchResponse = <T>(
  entities: T[],
  lastEvaluatedItemId?: string,
) => ({
  entities,
  lastEvaluatedItemId: lastEvaluatedItemId ?? null,
});

export const createSecurityReport = () => ({
  "damaged.tf": [createMisconfiguration()],
});
