import { render } from "@testing-library/react";
import ModuleDependencies from "./ModuleDependencies";

describe("<ModuleDependencies /> spec", () => {
  it("renders the ModuleDependencies", () => {
    const container = render(
      <ModuleDependencies
        modules={[
          { source: "terraform-aws-modules/security-group/aws", version: "" },
        ]}
        providers={[{ name: "aws" }]}
      />,
    );
    expect(container).toMatchSnapshot();
  });
});
