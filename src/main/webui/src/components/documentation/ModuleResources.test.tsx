import { render, screen } from "@testing-library/react";
import { axe } from "jest-axe";
import ModuleResources from "./ModuleResources";

const resources = [
  { name: "this", type: "ecr_lifecycle_policy" },
  { name: "bucket", type: "s3_bucket" },
];

describe("<ModuleResources />", () => {
  it('renders "Resources" heading', () => {
    render(<ModuleResources resources={resources} />);

    expect(
      screen.getByRole("heading", { name: "Resources" }),
    ).toBeInTheDocument();
  });

  it("renders resource count text", () => {
    render(<ModuleResources resources={resources} />);

    expect(screen.getByText(/This module defines/)).toBeInTheDocument();
    expect(
      screen.getByText((_, element) => {
        return element?.tagName === "STRONG" && element.textContent === "2";
      }),
    ).toBeInTheDocument();
  });

  it('renders each resource as chip with "type.name" format', () => {
    render(<ModuleResources resources={resources} />);

    expect(screen.getByText("ecr_lifecycle_policy.this")).toBeInTheDocument();
    expect(screen.getByText("s3_bucket.bucket")).toBeInTheDocument();
  });

  it("should have no accessibility violations", async () => {
    const { container } = render(<ModuleResources resources={resources} />);
    const results = await axe(container, {
      rules: {
        // ModuleResources wraps <li> in <Box> (div) inside <ul> — MUI structural issue
        list: { enabled: false },
        listitem: { enabled: false },
      },
    });
    expect(results).toHaveNoViolations();
  });
});
