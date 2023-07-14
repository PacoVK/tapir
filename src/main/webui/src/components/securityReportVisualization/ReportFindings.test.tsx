import React from "react";
import { render } from "@testing-library/react";
import ReportFindings from "./ReportFindings";

const fakeResponse = {
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
};

describe("<ReportFindings /> spec", () => {
  it("renders the ReportFindings", () => {
    const container = render(
      <ReportFindings reports={fakeResponse.securityReport} />,
    );
    expect(container).toMatchSnapshot();
  });
});
