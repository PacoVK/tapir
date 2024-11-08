import { render } from "@testing-library/react";
import NavigationDrawer from "./NavigationDrawer";
import { BrowserRouter } from "react-router-dom";

describe("<NavigationDrawer /> spec", () => {
  it("renders the NavigationDrawer", () => {
    const view = render(
      <BrowserRouter
        future={{
          v7_startTransition: true,
          v7_relativeSplatPath: true,
        }}
      >
        <NavigationDrawer />
      </BrowserRouter>,
    );
    expect(view).toMatchSnapshot();
  });
});
