import { screen } from "@testing-library/react";
import { axe } from "jest-axe";
import NavigationDrawer from "./NavigationDrawer";
import { renderWithProviders } from "../../test/RouterUtils";
import { createUser, createAdminUser } from "../../test/mocks/fixtures";

describe("<NavigationDrawer />", () => {
  it('renders "Modules" navigation link', () => {
    renderWithProviders(<NavigationDrawer />);

    expect(screen.getByText("Modules")).toBeInTheDocument();
  });

  it('renders "Providers" navigation link', () => {
    renderWithProviders(<NavigationDrawer />);

    expect(screen.getByText("Providers")).toBeInTheDocument();
  });

  it('does NOT render "Management" when not admin', () => {
    renderWithProviders(<NavigationDrawer />, {
      user: createUser(),
    });

    expect(screen.queryByText("Management")).not.toBeInTheDocument();
  });

  it('renders "Management" when admin', () => {
    renderWithProviders(<NavigationDrawer />, {
      user: createAdminUser(),
    });

    expect(screen.getByText("Management")).toBeInTheDocument();
  });

  it("should have no accessibility violations", async () => {
    const { container } = renderWithProviders(<NavigationDrawer />, {
      user: createUser(),
    });
    const results = await axe(container, {
      rules: {
        // MUI List with Link component renders <a> as direct child of <ul>
        list: { enabled: false },
      },
    });
    expect(results).toHaveNoViolations();
  });
});
