import { render, screen } from "@testing-library/react";
import { axe } from "jest-axe";
import NotFoundPage from "./404";

describe("<NotFoundPage />", () => {
  it("renders '404 - Page not found' heading", () => {
    render(<NotFoundPage />);
    expect(screen.getByText("404 - Page not found")).toBeInTheDocument();
  });

  it("renders Tapir logo image", () => {
    render(<NotFoundPage />);
    expect(screen.getByAltText("Tapir logo")).toBeInTheDocument();
  });

  it("has no accessibility violations", async () => {
    const { container } = render(<NotFoundPage />);
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
