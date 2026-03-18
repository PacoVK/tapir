import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import { axe } from "jest-axe";
import AppRouter from "./AppRouter";
import { UserProvider } from "./context/UserContext";
import { createUser, createAdminUser } from "../test/mocks/fixtures";
import { User } from "../types";

const renderAppRouter = (route: string, user: User = createUser()) => {
  return render(
    <UserProvider fetchedUser={user}>
      <MemoryRouter
        initialEntries={[route]}
        future={{ v7_startTransition: true, v7_relativeSplatPath: true }}
      >
        <AppRouter />
      </MemoryRouter>
    </UserProvider>,
  );
};

describe("<AppRouter />", () => {
  it('renders ModuleOverview at "/"', async () => {
    renderAppRouter("/");

    expect(await screen.findByLabelText("Search module")).toBeInTheDocument();
  });

  it('renders ProviderOverview at "/providers"', async () => {
    renderAppRouter("/providers");

    expect(await screen.findByLabelText("Search provider")).toBeInTheDocument();
  });

  it('renders 404 page at "/nonexistent"', async () => {
    renderAppRouter("/nonexistent");

    expect(await screen.findByText("404 - Page not found")).toBeInTheDocument();
  });

  it('does NOT render ManagementView at "/management" when not admin', async () => {
    renderAppRouter("/management", createUser());

    expect(await screen.findByText("404 - Page not found")).toBeInTheDocument();
  });

  it('renders ManagementView at "/management" when admin', async () => {
    renderAppRouter("/management", createAdminUser());

    expect(await screen.findByText("Tapir Management")).toBeInTheDocument();
  });

  it("should have no accessibility violations", async () => {
    const { container } = renderAppRouter("/");

    // Wait for async content to load
    await screen.findByLabelText("Search module");

    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
