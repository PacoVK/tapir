import { render } from "@testing-library/react";
import ModuleInput from "./ModuleInput";

describe("<ModuleInput /> spec", () => {
  it("renders the ModuleInput", () => {
    const container = render(
      <ModuleInput
        inputs={[
          {
            name: "repositories",
            type: "map(object({force_delete : optional(bool, false)\n    tags : optional(map(any))\n  }))",
            description: "List of repositories to create",
            required: true,
          },
        ]}
      />
    );
    expect(container).toMatchSnapshot();
  });
});
