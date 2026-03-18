import { render, screen } from "@testing-library/react";
import { axe } from "jest-axe";
import ReportFindings from "./ReportFindings";

const fakeReports = {
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
};

describe("<ReportFindings />", () => {
  it('renders "Security Report" heading', () => {
    render(<ReportFindings reports={fakeReports} />);

    expect(
      screen.getByRole("heading", { name: "Security Report" }),
    ).toBeInTheDocument();
  });

  it("renders Trivy link", () => {
    render(<ReportFindings reports={fakeReports} />);

    const trivyLink = screen.getByRole("link", { name: "Trivy" });
    expect(trivyLink).toBeInTheDocument();
    expect(trivyLink).toHaveAttribute("href", "https://trivy.dev/");
  });

  it("renders accordion with target name and finding count", () => {
    render(<ReportFindings reports={fakeReports} />);

    expect(screen.getByText("damaged.tf - Findings: 1")).toBeInTheDocument();
  });

  it("should have no accessibility violations", async () => {
    const { container } = render(<ReportFindings reports={fakeReports} />);
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
