import React from "react";
import { render } from "@testing-library/react";
import ProviderOverview from "./ProviderOverview";

describe("<ProviderOverview /> spec", () => {
  it("renders the provider overview", () => {
    const container = render(<ProviderOverview />);
    expect(container).toMatchSnapshot();
  });
});
