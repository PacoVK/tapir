import { render } from "@testing-library/react";
import ModuleOutput from "./ModuleOutput";

describe("<ModuleOutput /> spec", () => {
  it("renders the ModuleOutput", () => {
    const container = render(
      <ModuleOutput
        outputs={[{ name: "github_roles" }, { name: "registries" }]}
      />,
    );
    expect(container).toMatchSnapshot();
  });
});
