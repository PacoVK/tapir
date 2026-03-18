import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { axe } from "jest-axe";
import ModuleAnalysisTab from "./ModuleAnalysisTab";

const documentation = {
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
};

const securityReport = {
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
};

describe("<ModuleAnalysisTab />", () => {
  it("renders tab labels with counts", () => {
    render(
      <ModuleAnalysisTab
        version="1.0.2"
        reports={securityReport}
        documentation={documentation}
      />,
    );

    expect(screen.getByRole("tab", { name: "Inputs (1)" })).toBeInTheDocument();
    expect(
      screen.getByRole("tab", { name: "Outputs (2)" }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("tab", { name: "Dependencies (2)" }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("tab", { name: "Resources (1)" }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("tab", { name: "Security Report" }),
    ).toBeInTheDocument();
  });

  it("shows Inputs tab content by default", () => {
    render(
      <ModuleAnalysisTab
        version="1.0.2"
        reports={securityReport}
        documentation={documentation}
      />,
    );

    // The Inputs tab is active by default, so the input name should be visible
    expect(screen.getByText("repositories")).toBeInTheDocument();
  });

  it('clicking "Security Report" tab shows "No report available" when reports is null', async () => {
    const user = userEvent.setup();
    render(
      <ModuleAnalysisTab
        version="1.0.2"
        reports={null}
        documentation={documentation}
      />,
    );

    await user.click(screen.getByRole("tab", { name: "Security Report" }));

    expect(
      screen.getByText("No report available for module version: 1.0.2"),
    ).toBeInTheDocument();
  });

  it("returns null when documentation is null", () => {
    const { container } = render(
      <ModuleAnalysisTab version="1.0.2" reports={null} documentation={null} />,
    );

    expect(container.innerHTML).toBe("");
  });

  it("should have no accessibility violations", async () => {
    const { container } = render(
      <ModuleAnalysisTab
        version="1.0.2"
        reports={securityReport}
        documentation={documentation}
      />,
    );
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
