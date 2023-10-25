import { render } from "@testing-library/react";
import SnackBar from "./SnackBar";

describe("<SnackBar /> spec", () => {
  it("renders the SnackBar", () => {
    const view = render(
      <SnackBar
        open={true}
        severity={"info"}
        message={"hello world"}
        handleClose={() => {}}
      />,
    );
    expect(view).toMatchSnapshot();
  });
});
