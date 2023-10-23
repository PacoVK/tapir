import React from "react";
import { render } from "@testing-library/react";
import ProviderDetails from "./ProviderDetails";

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
  useLocation: () => ({
    pathname: "localhost:3000/providers/foo/bar",
  }),
}));
describe("<ProviderDetails /> spec", () => {
  it("renders the ProviderDetails", () => {
    const container = render(<ProviderDetails />);
    expect(container).toMatchSnapshot();
  });
});
