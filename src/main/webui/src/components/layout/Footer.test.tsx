import { render } from "@testing-library/react";
import Footer from "./Footer";

describe("<Footer /> spec", () => {
  it("renders the Footer", () => {
    const view = render(<Footer />);
    expect(view).toMatchSnapshot();
  });
});
