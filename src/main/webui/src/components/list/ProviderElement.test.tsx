import ProviderElement from "./ProviderElement";
import { renderWithRouter } from "../../test/RouterUtils";

describe("<ProviderElement /> spec", () => {
  it("renders the ProviderElement", () => {
    const view = renderWithRouter(
      <ProviderElement
        provider={{
          id: "foo/bar/baz",
          namespace: "bar",
          type: "baz",
          published_at: "2021-10-10T10:10:10.000Z",
          versions: [{ version: "1.0" }],
          downloads: 0,
        }}
      />,
    );
    expect(view).toMatchSnapshot();
  });
});
