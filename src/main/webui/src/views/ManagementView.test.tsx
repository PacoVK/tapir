import { screen } from "@testing-library/react";
import { axe } from "jest-axe";
import { http, HttpResponse } from "msw";
import ManagementView from "./ManagementView";
import { renderWithRouter } from "../test/RouterUtils";
import { server } from "../test/mocks/server";
import { createSearchResponse } from "../test/mocks/fixtures";

describe("<ManagementView />", () => {
  it("renders 'Tapir Management' heading", async () => {
    renderWithRouter(<ManagementView />);
    expect(await screen.findByText("Tapir Management")).toBeInTheDocument();
  });

  it("shows deploy key list after fetch", async () => {
    renderWithRouter(<ManagementView />);
    expect(await screen.findByText("dk-test-key-001")).toBeInTheDocument();
  });

  it("shows not-found message when API returns empty entities", async () => {
    server.use(
      http.get("*/search/deploykeys", () => {
        return HttpResponse.json(createSearchResponse([]));
      }),
    );

    renderWithRouter(<ManagementView />);
    expect(
      await screen.findByText("Found this Tapir, but no keys"),
    ).toBeInTheDocument();
  });

  it("has no accessibility violations", async () => {
    const { container } = renderWithRouter(<ManagementView />);
    await screen.findByText("dk-test-key-001");
    const results = await axe(container, {
      rules: {
        // MUI ListItem renders <li> inside Stack (not a direct <ul> child)
        listitem: { enabled: false },
      },
    });
    expect(results).toHaveNoViolations();
  });
});
