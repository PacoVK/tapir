import { render } from "@testing-library/react";
import NavigationDrawer from "./NavigationDrawer";

describe("<NavigationDrawer /> spec", () => {
  it("renders the NavigationDrawer", () => {
    const container = render(<NavigationDrawer />);
    expect(container).toMatchSnapshot();
  });
});
