import { render, screen } from "@testing-library/react";
import { axe } from "jest-axe";
import ModuleDependencies from "./ModuleDependencies";

describe("<ModuleDependencies />", () => {
  const modules = [
    { source: "terraform-aws-modules/security-group/aws", version: "" },
    { source: "terraform-aws-modules/vpc/aws", version: "3.0.0" },
  ];

  const providers = [
    { name: "aws" },
    { name: "google", version: "4.0.0", alias: "gcp" },
  ];

  it('renders "Module Dependencies" heading', () => {
    render(<ModuleDependencies modules={modules} providers={providers} />);

    expect(
      screen.getByRole("heading", { name: "Module Dependencies" }),
    ).toBeInTheDocument();
  });

  it('renders "Provider Dependencies" heading', () => {
    render(<ModuleDependencies modules={modules} providers={providers} />);

    expect(
      screen.getByRole("heading", { name: "Provider Dependencies" }),
    ).toBeInTheDocument();
  });

  it("renders module source name in bold", () => {
    render(<ModuleDependencies modules={modules} providers={providers} />);

    const boldSources = screen.getAllByText(
      (_, element) =>
        element?.tagName === "STRONG" &&
        (element.textContent === "terraform-aws-modules/security-group/aws" ||
          element.textContent === "terraform-aws-modules/vpc/aws"),
    );
    expect(boldSources).toHaveLength(2);
  });

  it('renders "latest" when module version is empty string', () => {
    render(<ModuleDependencies modules={modules} providers={providers} />);

    const latestElements = screen.getAllByText("latest");
    // At least one "latest" should exist for the empty-version module
    expect(latestElements.length).toBeGreaterThanOrEqual(1);
  });

  it("renders provider name in bold", () => {
    render(<ModuleDependencies modules={modules} providers={providers} />);

    expect(
      screen.getByText(
        (_, element) =>
          element?.tagName === "STRONG" && element.textContent === "aws",
      ),
    ).toBeInTheDocument();
    expect(
      screen.getByText(
        (_, element) =>
          element?.tagName === "STRONG" && element.textContent === "google",
      ),
    ).toBeInTheDocument();
  });

  it('renders provider version, or "latest" when undefined', () => {
    render(<ModuleDependencies modules={modules} providers={providers} />);

    expect(screen.getByText("4.0.0")).toBeInTheDocument();
    // aws provider has no version, so "latest" should appear for it
    const latestElements = screen.getAllByText("latest");
    expect(latestElements.length).toBeGreaterThanOrEqual(2); // one for module, one for aws provider
  });

  it("renders provider alias when present", () => {
    render(<ModuleDependencies modules={modules} providers={providers} />);

    expect(screen.getByText("gcp")).toBeInTheDocument();
  });

  it('renders "no external module dependencies" message when modules is null', () => {
    render(<ModuleDependencies modules={null as any} providers={providers} />);

    expect(
      screen.getByText("This module has no external module dependencies"),
    ).toBeInTheDocument();
  });

  it("should have no accessibility violations", async () => {
    const { container } = render(
      <ModuleDependencies modules={modules} providers={providers} />,
    );
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
