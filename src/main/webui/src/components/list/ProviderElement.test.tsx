import { screen } from "@testing-library/react";
import { axe } from "jest-axe";
import ProviderElement from "./ProviderElement";
import { renderWithRouter } from "../../test/RouterUtils";
import { createProviderWithVersionMap } from "../../test/mocks/fixtures";

describe("<ProviderElement />", () => {
  it("renders provider name as namespace-type", () => {
    renderWithRouter(
      <ProviderElement provider={createProviderWithVersionMap() as any} />,
    );
    expect(screen.getByText("hashicorp-aws")).toBeInTheDocument();
  });

  it("renders latest version chip", () => {
    renderWithRouter(
      <ProviderElement provider={createProviderWithVersionMap() as any} />,
    );
    expect(screen.getByText("Latest version: 5.0.0")).toBeInTheDocument();
  });

  it("renders last published at chip", () => {
    renderWithRouter(
      <ProviderElement provider={createProviderWithVersionMap() as any} />,
    );
    expect(
      screen.getByText("Last published at: 20-02-2024"),
    ).toBeInTheDocument();
  });

  it("has no accessibility violations", async () => {
    const { container } = renderWithRouter(
      <ProviderElement provider={createProviderWithVersionMap() as any} />,
    );
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
