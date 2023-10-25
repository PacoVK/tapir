import React from "react";
import { render } from "@testing-library/react";
import ModuleDetails from "./ModuleDetails";

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
  useLocation: () => ({
    pathname: "localhost:3000/module/foo/bar/aws",
  }),
}));
describe("<ModuleDetails /> spec", () => {
  it("renders the ModuleDetails", () => {
    const view = render(<ModuleDetails />);
    expect(view).toMatchSnapshot();
  });
});
