import { render, screen } from "@testing-library/react";
import { axe } from "jest-axe";
import Footer from "./Footer";

describe("<Footer />", () => {
  beforeEach(() => {
    import.meta.env.VITE_VERSION = "1.0.0";
  });

  it("renders the version string", () => {
    render(<Footer />);
    expect(
      screen.getByText("Terraform Private Registry v1.0.0"),
    ).toBeInTheDocument();
  });

  it("renders GitHub link with correct href", () => {
    render(<Footer />);
    const link = screen.getByRole("link", { name: /github/i });
    expect(link).toHaveAttribute("href", "https://github.com/PacoVK/tapir");
  });

  it("renders the current year", () => {
    render(<Footer />);
    const currentYear = new Date().getFullYear().toString();
    expect(screen.getByText(new RegExp(currentYear))).toBeInTheDocument();
  });

  it("has no accessibility violations", async () => {
    const { container } = render(<Footer />);
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
