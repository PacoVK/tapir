import { render } from "@testing-library/react";
import ModuleOutput from "./ModuleOutput";

describe("<ModuleOutput /> spec", () => {
  it("renders the ModuleOutput", () => {
    const view = render(
      <ModuleOutput
        outputs={[{ name: "github_roles" }, { name: "registries" }]}
      />,
    );
    expect(view).toMatchSnapshot();
  });
});
