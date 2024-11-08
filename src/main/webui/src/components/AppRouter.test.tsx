import React from "react";
import { render } from "@testing-library/react";
import AppRouter from "./AppRouter";
import { BrowserRouter } from "react-router-dom";

describe("<AppRouter /> spec", () => {
  it("renders the AppRouter", () => {
    const view = render(
      <BrowserRouter
        future={{
          v7_startTransition: true,
          v7_relativeSplatPath: true,
        }}
      >
        <AppRouter />
      </BrowserRouter>,
    );
    expect(view).toMatchSnapshot();
  });
});
