import { render } from "@testing-library/react";
import DeployKeyForm from "./DeployKeyForm";

describe("<DeployKeyForm /> spec", () => {
  it("renders the DeployKeyForm", () => {
    const view = render(<DeployKeyForm notifyUser={() => {}} />);
    expect(view).toMatchSnapshot();
  });
});
