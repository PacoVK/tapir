import React from "react";
import { render } from "@testing-library/react";
import AppRouter from "./AppRouter";

describe("<AppRouter /> spec", () => {
  it("renders the AppRouter", () => {
    const container = render(<AppRouter />);
    expect(container).toMatchSnapshot();
  });
});
