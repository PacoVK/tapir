import React from "react";
import { render } from "@testing-library/react";
import Overview from "./Overview";

describe("<Overview /> spec", () => {
  it("renders the overview", () => {
    const container = render(<Overview />);
    expect(container).toMatchSnapshot();
  });
});
