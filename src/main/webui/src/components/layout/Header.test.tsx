import { render } from "@testing-library/react";
import Header from "./Header";

describe("<Header /> spec", () => {
  it("renders the Header", () => {
    const view = render(<Header />);
    expect(view).toMatchSnapshot();
  });
});
