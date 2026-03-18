import { render, screen } from "@testing-library/react";
import { axe } from "jest-axe";
import NotFoundInfo from "./NotFoundInfo";

describe("<NotFoundInfo />", () => {
  it("renders the Tapir logo image", () => {
    render(<NotFoundInfo entity="modules" />);
    expect(screen.getByAltText("Tapir logo")).toBeInTheDocument();
  });

  it("renders text containing the entity prop value", () => {
    render(<NotFoundInfo entity="modules" />);
    expect(
      screen.getByText("Found this Tapir, but no modules"),
    ).toBeInTheDocument();
  });

  it("renders with different entity values", () => {
    const { unmount } = render(<NotFoundInfo entity="providers" />);
    expect(
      screen.getByText("Found this Tapir, but no providers"),
    ).toBeInTheDocument();
    unmount();

    render(<NotFoundInfo entity="deploy keys" />);
    expect(
      screen.getByText("Found this Tapir, but no deploy keys"),
    ).toBeInTheDocument();
  });

  it("has no accessibility violations", async () => {
    const { container } = render(<NotFoundInfo entity="modules" />);
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
