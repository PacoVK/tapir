import { render, screen } from "@testing-library/react";
import { axe } from "jest-axe";
import ModuleOutput from "./ModuleOutput";

const outputs = [
  { name: "github_roles", description: "The GitHub roles" },
  { name: "registries", description: "The registries output" },
];

describe("<ModuleOutput />", () => {
  it("renders each output name in bold", () => {
    render(<ModuleOutput outputs={outputs} />);

    const boldElements = screen.getAllByText(
      (_, element) =>
        element?.tagName === "STRONG" &&
        (element.textContent === "github_roles" ||
          element.textContent === "registries"),
    );
    expect(boldElements).toHaveLength(2);
    expect(boldElements[0]).toHaveTextContent("github_roles");
    expect(boldElements[1]).toHaveTextContent("registries");
  });

  it("renders output description", () => {
    render(<ModuleOutput outputs={outputs} />);

    expect(screen.getByText("The GitHub roles")).toBeInTheDocument();
    expect(screen.getByText("The registries output")).toBeInTheDocument();
  });

  it('renders "This module has no outputs" when empty', () => {
    render(<ModuleOutput outputs={[]} />);

    expect(screen.getByText("This module has no outputs")).toBeInTheDocument();
  });

  it("should have no accessibility violations", async () => {
    const { container } = render(<ModuleOutput outputs={outputs} />);
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
