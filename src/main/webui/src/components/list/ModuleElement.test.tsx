import ModuleElement from "./ModuleElement";
import { ProviderType } from "../../types";
import { renderWithRouter } from "../../test/RouterUtils";

describe("<ModuleElement /> spec", () => {
  it("renders the ModuleElement", () => {
    const container = renderWithRouter(
      <ModuleElement
        module={{
          id: "foo/bar/baz",
          namespace: "foo",
          name: "bar",
          provider: ProviderType.AWS,
          downloads: 0,
          versions: [{ version: "1.0" }],
          published_at: "2021-10-10T10:10:10.000Z",
        }}
      />,
    );
    expect(container).toMatchSnapshot();
  });
});
