import { render } from "@testing-library/react";
import ModuleInput from "./ModuleInput";

describe("<ModuleInput /> spec", () => {
  it("renders the ModuleInput", () => {
    const view = render(
      <ModuleInput
        inputs={[
          {
            name: "repositories",
            type: "map(object({force_delete : optional(bool, false)\n    tags : optional(map(any))\n  }))",
            description: "List of repositories to create",
            required: false,
          },
            {
                name: "cidr_blocks",
                type: "string",
                description: "CIDR block for the VPC",
                required: true,
                default: "10.0.0.0/8",
            },
        ]}
      />,
    );
    expect(view).toMatchSnapshot();
  });
});
