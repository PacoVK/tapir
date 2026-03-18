import { screen } from "@testing-library/react";
import { axe } from "jest-axe";
import ModuleElement from "./ModuleElement";
import { renderWithRouter } from "../../test/RouterUtils";
import { createModule } from "../../test/mocks/fixtures";

describe("<ModuleElement />", () => {
  it("renders module name as namespace/name", () => {
    renderWithRouter(<ModuleElement module={createModule()} />);
    expect(screen.getByText("foo/bar")).toBeInTheDocument();
  });

  it("renders total downloads chip", () => {
    renderWithRouter(<ModuleElement module={createModule()} />);
    expect(screen.getByText("Total downloads: 42")).toBeInTheDocument();
  });

  it("renders latest version chip", () => {
    renderWithRouter(<ModuleElement module={createModule()} />);
    expect(screen.getByText("Latest version: 1.0.0")).toBeInTheDocument();
  });

  it("renders last published at chip", () => {
    renderWithRouter(<ModuleElement module={createModule()} />);
    expect(
      screen.getByText("Last published at: 15-01-2024"),
    ).toBeInTheDocument();
  });

  it("renders provider logo image", () => {
    renderWithRouter(<ModuleElement module={createModule()} />);
    expect(screen.getByAltText("Provider logo")).toBeInTheDocument();
  });

  it("has no accessibility violations", async () => {
    const { container } = renderWithRouter(
      <ModuleElement module={createModule()} />,
    );
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
