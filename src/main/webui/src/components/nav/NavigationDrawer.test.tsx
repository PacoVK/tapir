import { render } from "@testing-library/react";
import NavigationDrawer from "./NavigationDrawer";

describe("<NavigationDrawer /> spec", () => {
  it("renders the NavigationDrawer", () => {
    const view = render(<NavigationDrawer />);
    expect(view).toMatchSnapshot();
  });
});
