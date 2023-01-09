import {render} from "@testing-library/react";
import Header from "./Header";

describe("<Header /> spec", () => {
    it("renders the Header", () => {
        const container = render(<Header />);
        expect(container).toMatchSnapshot();
    });
});