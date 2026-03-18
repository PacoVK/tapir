import { screen } from "@testing-library/react";
import { axe } from "jest-axe";
import { http, HttpResponse } from "msw";
import ModuleOverview from "./ModuleOverview";
import { renderWithRouter } from "../test/RouterUtils";
import { server } from "../test/mocks/server";
import { createSearchResponse } from "../test/mocks/fixtures";

describe("<ModuleOverview />", () => {
  it("renders module list after fetch resolves", async () => {
    renderWithRouter(<ModuleOverview />);
    expect(await screen.findByText("foo/bar")).toBeInTheDocument();
  });

  it("shows not-found message when API returns empty entities", async () => {
    server.use(
      http.get("*/search/modules", () => {
        return HttpResponse.json(createSearchResponse([]));
      }),
    );

    renderWithRouter(<ModuleOverview />);
    expect(
      await screen.findByText("Found this Tapir, but no modules"),
    ).toBeInTheDocument();
  });

  it("renders search input with 'Search module' label", () => {
    renderWithRouter(<ModuleOverview />);
    expect(screen.getByLabelText("Search module")).toBeInTheDocument();
  });

  it("has no accessibility violations", async () => {
    const { container } = renderWithRouter(<ModuleOverview />);
    await screen.findByText("foo/bar");
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
