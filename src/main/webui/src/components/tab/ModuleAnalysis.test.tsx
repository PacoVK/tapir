import React from "react";
import { render } from "@testing-library/react";
import ModuleAnalysisTab from "./ModuleAnalysisTab";

const fakeResponse = {
  id: "paco-fooo-aws-1.0.2",
  moduleName: "fooo",
  moduleVersion: "1.0.2",
  moduleNamespace: "paco",
  provider: "aws",
  securityReport: {
    "damaged.tf": [
      {
        rule_id: "AVD-AWS-0107",
        long_id: "aws-ec2-no-public-ingress-sgr",
        rule_description:
          "An ingress security group rule allows traffic from /0.",
        rule_provider: "aws",
        rule_service: "ec2",
        impact: "Your port exposed to the internet",
        resolution: "Set a more restrictive cidr range",
        links: [
          "https://aquasecurity.github.io/tfsec/v1.28.1/checks/aws/ec2/no-public-ingress-sgr/",
          "https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule#cidr_blocks",
        ],
        description: "Security group rule allows ingress from public internet.",
        severity: "CRITICAL",
        warning: false,
        status: 0,
        resource: "aws_security_group.no_issue",
        location: {
          filename: "/foo/bar/damaged.tf",
          start_line: 31,
          end_line: 31,
        },
      },
    ],
  },
  documentation: {
    inputs: [
      {
        name: "repositories",
        type: "map(object({force_delete : optional(bool, false)\n    tags : optional(map(any))\n  }))",
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
  },
};

describe("<ModuleAnalysisTab /> spec", () => {
  it("renders the ModuleAnalysisTab", () => {
    const view = render(
      <ModuleAnalysisTab
        version={fakeResponse.moduleVersion}
        reports={fakeResponse.securityReport}
        documentation={fakeResponse.documentation}
      />,
    );
    expect(view).toMatchSnapshot();
  });
});
