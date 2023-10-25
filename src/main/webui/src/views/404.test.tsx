import { render } from "@testing-library/react";
import NotFoundPage from "./404";

describe("<NotFoundPage /> spec", () => {
  it("renders the NotFoundPage", () => {
    const container = render(<NotFoundPage />);
    expect(container).toMatchSnapshot();
  });
});
