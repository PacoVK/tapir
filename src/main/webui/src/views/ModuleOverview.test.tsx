import React from "react";
import { render } from "@testing-library/react";
import ModuleOverview from "./ModuleOverview";

describe("<ModuleOverview /> spec", () => {
  it("renders the module overview", () => {
    const container = render(<ModuleOverview />);
    expect(container).toMatchSnapshot();
  });
});
