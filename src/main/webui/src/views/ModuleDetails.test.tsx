import React from "react";
import { render } from "@testing-library/react";
import ModuleDetails from "./ModuleDetails";

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useLocation: () => ({
    pathname: "localhost:3000/module/foo/bar/aws",
  }),
}));
describe("<ModuleDetails /> spec", () => {
  it("renders the ModuleDetails", () => {
    const container = render(<ModuleDetails />);
    expect(container).toMatchSnapshot();
  });
});
