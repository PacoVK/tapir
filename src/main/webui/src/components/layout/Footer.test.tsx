import { render } from "@testing-library/react";
import Footer from "./Footer";

describe("<Footer /> spec", () => {
  it("renders the Footer", () => {
    const container = render(<Footer />);
    expect(container).toMatchSnapshot();
  });
});
