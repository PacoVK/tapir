import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { axe } from "jest-axe";
import Header from "./Header";
import { renderWithProviders } from "../../test/RouterUtils";
import { createUser } from "../../test/mocks/fixtures";

describe("<Header />", () => {
  it("renders the title", () => {
    renderWithProviders(<Header />);
    expect(
      screen.getByText("Tapir - Private Terraform Registry"),
    ).toBeInTheDocument();
  });

  it("renders user avatar with first letter of user name", () => {
    renderWithProviders(<Header />, {
      user: createUser({ name: "Alice" }),
    });
    expect(screen.getByText("A")).toBeInTheDocument();
  });

  it("opens menu on avatar click showing About and Logout", async () => {
    const user = userEvent.setup();
    renderWithProviders(<Header />, {
      user: createUser({ name: "Alice" }),
    });

    const avatarButton = screen.getByText("A");
    await user.click(avatarButton);

    await waitFor(() => {
      expect(screen.getByText("About")).toBeInTheDocument();
      expect(screen.getByText("Logout")).toBeInTheDocument();
    });
  });

  it("has no accessibility violations", async () => {
    const { container } = renderWithProviders(<Header />, {
      user: createUser({ name: "Alice" }),
    });
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
