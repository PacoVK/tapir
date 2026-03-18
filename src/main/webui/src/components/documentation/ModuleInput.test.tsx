import { render, screen } from "@testing-library/react";
import { axe } from "jest-axe";
import ModuleInput from "./ModuleInput";

const requiredInput = {
  name: "cidr_blocks",
  type: "string",
  description: "CIDR block for the VPC",
  required: true,
  default: "10.0.0.0/8",
};

const optionalInput = {
  name: "repositories",
  type: "map(object({force_delete : optional(bool, false)\n    tags : optional(map(any))\n  }))",
  description: "List of repositories to create",
  required: false,
};

describe("<ModuleInput />", () => {
  it("renders each input name in bold", () => {
    render(<ModuleInput inputs={[requiredInput, optionalInput]} />);

    const boldElements = screen.getAllByText(
      (_, element) =>
        element?.tagName === "STRONG" &&
        (element.textContent === "cidr_blocks" ||
          element.textContent === "repositories"),
    );
    expect(boldElements).toHaveLength(2);
    expect(boldElements[0]).toHaveTextContent("cidr_blocks");
    expect(boldElements[1]).toHaveTextContent("repositories");
  });

  it("renders input description text", () => {
    render(<ModuleInput inputs={[requiredInput, optionalInput]} />);

    expect(screen.getByText("CIDR block for the VPC")).toBeInTheDocument();
    expect(
      screen.getByText("List of repositories to create"),
    ).toBeInTheDocument();
  });

  it("shows required badge dot for required inputs", () => {
    const { container } = render(
      <ModuleInput inputs={[requiredInput, optionalInput]} />,
    );

    const badges = container.querySelectorAll(".MuiBadge-root");
    expect(badges).toHaveLength(2);

    const requiredBadge = badges[0];
    const optionalBadge = badges[1];

    // Required input should have a visible badge dot
    const requiredDot = requiredBadge.querySelector(".MuiBadge-dot");
    expect(requiredDot).toBeInTheDocument();
    expect(requiredDot).not.toHaveClass("MuiBadge-invisible");

    // Optional input should have an invisible badge dot
    const optionalDot = optionalBadge.querySelector(".MuiBadge-dot");
    expect(optionalDot).toBeInTheDocument();
    expect(optionalDot).toHaveClass("MuiBadge-invisible");
  });

  it('renders "This module has no inputs" when inputs array is empty', () => {
    render(<ModuleInput inputs={[]} />);

    expect(screen.getByText("This module has no inputs")).toBeInTheDocument();
  });

  it("should have no accessibility violations", async () => {
    const { container } = render(
      <ModuleInput inputs={[requiredInput, optionalInput]} />,
    );
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
