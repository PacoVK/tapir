import React from "react";
import { render } from "@testing-library/react";
import AppRouter from "./AppRouter";
import { BrowserRouter } from "react-router-dom";

describe("<AppRouter /> spec", () => {
  it("renders the AppRouter", () => {
    const view = render(
      <BrowserRouter>
        <AppRouter />
      </BrowserRouter>
    );
    expect(view).toMatchSnapshot();
  });
});
