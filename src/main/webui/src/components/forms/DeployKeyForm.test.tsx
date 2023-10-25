import { render } from "@testing-library/react";
import DeployKeyForm from "./DeployKeyForm";

describe("<DeployKeyForm /> spec", () => {
  it("renders the DeployKeyForm", () => {
    const container = render(<DeployKeyForm notifyUser={() => {}} />);
    expect(container).toMatchSnapshot();
  });
});
