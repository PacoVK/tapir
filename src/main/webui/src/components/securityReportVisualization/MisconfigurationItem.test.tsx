import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { axe } from "jest-axe";
import MisconfigurationItem from "./MisconfigurationItem";

const misconfiguration = {
  severity: "CRITICAL",
  rule_description: "An ingress security group rule allows traffic from /0.",
  links: [
    "https://aquasecurity.github.io/tfsec/v1.28.1/checks/aws/ec2/no-public-ingress-sgr/",
    "https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule#cidr_blocks",
  ] as unknown as string,
  resolution: "Set a more restrictive cidr range",
  resource: "aws_security_group.no_issue",
  impact: "Your port exposed to the internet",
  location: {
    start_line: 38,
    end_line: 40,
    filename: "any.tf",
  },
  description: "Security group rule allows ingress from public internet.",
};

describe("<MisconfigurationItem />", () => {
  it("renders resource name and line range in accordion summary", () => {
    render(
      <MisconfigurationItem
        misconfiguration={misconfiguration}
        keyIdentifier="foo-bar-baz"
      />,
    );

    expect(
      screen.getByText(/aws_security_group\.no_issue, Line: 38 - 40/),
    ).toBeInTheDocument();
  });

  it("renders severity, violation, impact, solution in accordion details", async () => {
    const user = userEvent.setup();
    render(
      <MisconfigurationItem
        misconfiguration={misconfiguration}
        keyIdentifier="foo-bar-baz"
      />,
    );

    // Expand the accordion by clicking on the summary
    const summary = screen.getByText(
      /aws_security_group\.no_issue, Line: 38 - 40/,
    );
    await user.click(summary);

    expect(screen.getByText(/Severity:/)).toBeInTheDocument();
    expect(screen.getByText("CRITICAL")).toBeInTheDocument();
    expect(screen.getByText(/Violation:/)).toBeInTheDocument();
    expect(
      screen.getByText(
        "An ingress security group rule allows traffic from /0.",
      ),
    ).toBeInTheDocument();
    expect(screen.getByText(/Impact:/)).toBeInTheDocument();
    expect(
      screen.getByText("Your port exposed to the internet"),
    ).toBeInTheDocument();
    expect(screen.getByText(/Solution:/)).toBeInTheDocument();
    expect(
      screen.getByText("Set a more restrictive cidr range"),
    ).toBeInTheDocument();
  });

  it('renders "Read more" link', async () => {
    const user = userEvent.setup();
    render(
      <MisconfigurationItem
        misconfiguration={misconfiguration}
        keyIdentifier="foo-bar-baz"
      />,
    );

    await user.click(
      screen.getByText(/aws_security_group\.no_issue, Line: 38 - 40/),
    );

    const link = screen.getByRole("link", { name: "Read more" });
    expect(link).toBeInTheDocument();
    expect(link).toHaveAttribute(
      "href",
      "https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule#cidr_blocks",
    );
  });

  it("should have no accessibility violations", async () => {
    const { container } = render(
      <MisconfigurationItem
        misconfiguration={misconfiguration}
        keyIdentifier="foo-bar-baz"
      />,
    );
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
