import { render } from "@testing-library/react";
import MisconfigurationItem from "./MisconfigurationItem";

describe("<MisconfigurationItem /> spec", () => {
  it("renders the MisconfigurationItem", () => {
    const container = render(
      <MisconfigurationItem
        misconfiguration={{
          severity: "CRITICAL",
          rule_description: "foo",
          links: "https://fake.com",
          resolution: "bar",
          resource: "aws.securitygroup",
          impact: "baz",
          location: {
            start_line: 38,
            end_line: 40,
            filename: "any.tf",
          },
          description: "description",
        }}
        keyIdentifier={"foo-bar-baz"}
      />,
    );
    expect(container).toMatchSnapshot();
  });
});
