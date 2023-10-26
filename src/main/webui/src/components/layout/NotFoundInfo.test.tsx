import { render } from "@testing-library/react";
import NotFoundInfo from "./NotFoundInfo";

describe("<NotFoundInfo /> spec", () => {
  it("renders the NotFoundInfo", () => {
    const view = render(<NotFoundInfo entity={"foo"} />);
    expect(view).toMatchSnapshot();
  });
});
