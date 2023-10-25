import { render } from "@testing-library/react";
import ManagementView from "./ManagementView";

describe("<ManagementView /> spec", () => {
  it("renders the ManagementView", () => {
    const view = render(<ManagementView />);
    expect(view).toMatchSnapshot();
  });
});
