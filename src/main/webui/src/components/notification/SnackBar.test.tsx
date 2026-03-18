import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { axe } from "jest-axe";
import SnackBar from "./SnackBar";

describe("<SnackBar />", () => {
  it("renders message text when open is true", () => {
    render(
      <SnackBar
        open={true}
        severity="info"
        message="hello world"
        handleClose={() => {}}
      />,
    );

    expect(screen.getByText("hello world")).toBeInTheDocument();
  });

  it("does not render message when open is false", () => {
    render(
      <SnackBar
        open={false}
        severity="info"
        message="hello world"
        handleClose={() => {}}
      />,
    );

    expect(screen.queryByText("hello world")).not.toBeInTheDocument();
  });

  it("calls handleClose when close button is clicked", async () => {
    const handleClose = vi.fn();
    const user = userEvent.setup();

    render(
      <SnackBar
        open={true}
        severity="info"
        message="hello world"
        handleClose={handleClose}
      />,
    );

    const closeButton = screen.getByRole("button", { name: "Close" });
    await user.click(closeButton);

    expect(handleClose).toHaveBeenCalledTimes(1);
  });

  it("should have no accessibility violations", async () => {
    const { container } = render(
      <SnackBar
        open={true}
        severity="info"
        message="hello world"
        handleClose={() => {}}
      />,
    );
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
