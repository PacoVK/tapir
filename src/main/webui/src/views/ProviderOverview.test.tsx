import { screen } from "@testing-library/react";
import { axe } from "jest-axe";
import { http, HttpResponse } from "msw";
import ProviderOverview from "./ProviderOverview";
import { renderWithRouter } from "../test/RouterUtils";
import { server } from "../test/mocks/server";
import { createSearchResponse } from "../test/mocks/fixtures";

describe("<ProviderOverview />", () => {
  it("renders provider list after fetch resolves", async () => {
    renderWithRouter(<ProviderOverview />);
    expect(await screen.findByText("hashicorp-aws")).toBeInTheDocument();
  });

  it("shows not-found message when API returns empty entities", async () => {
    server.use(
      http.get("*/search/providers", () => {
        return HttpResponse.json(createSearchResponse([]));
      }),
    );

    renderWithRouter(<ProviderOverview />);
    expect(
      await screen.findByText("Found this Tapir, but no providers"),
    ).toBeInTheDocument();
  });

  it("renders search input with 'Search provider' label", () => {
    renderWithRouter(<ProviderOverview />);
    expect(screen.getByLabelText("Search provider")).toBeInTheDocument();
  });

  it("has no accessibility violations", async () => {
    const { container } = renderWithRouter(<ProviderOverview />);
    await screen.findByText("hashicorp-aws");
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
