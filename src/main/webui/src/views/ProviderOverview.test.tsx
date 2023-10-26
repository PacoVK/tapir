import React from "react";
import { render } from "@testing-library/react";
import ProviderOverview from "./ProviderOverview";

describe("<ProviderOverview /> spec", () => {
  it("renders the provider overview", () => {
    const view = render(<ProviderOverview />);
    expect(view).toMatchSnapshot();
  });
});
