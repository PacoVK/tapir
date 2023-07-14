import { render } from "@testing-library/react";
import ModuleResources from "./ModuleResources";

describe("<ModuleResources /> spec", () => {
  it("renders the ModuleResources", () => {
    const container = render(
      <ModuleResources
        resources={[
          {
            name: "this",
            type: "ecr_lifecycle_policy",
          },
        ]}
      />,
    );
    expect(container).toMatchSnapshot();
  });
});
