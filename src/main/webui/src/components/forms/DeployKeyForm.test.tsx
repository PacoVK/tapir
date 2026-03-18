import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { axe } from "jest-axe";
import { http, HttpResponse } from "msw";
import DeployKeyForm from "./DeployKeyForm";
import { server } from "../../test/mocks/server";

const getField = (name: string) =>
  screen.getByRole("textbox", { name: new RegExp(`^${name}\\b`) });

const queryField = (name: string) =>
  screen.queryByRole("textbox", { name: new RegExp(`^${name}\\b`) });

describe("<DeployKeyForm />", () => {
  it("renders module fields by default", () => {
    render(<DeployKeyForm notifyUser={() => {}} />);

    expect(getField("Source Repository URL")).toBeInTheDocument();
    expect(getField("Namespace")).toBeInTheDocument();
    expect(getField("Name")).toBeInTheDocument();
    expect(getField("Provider")).toBeInTheDocument();
  });

  it("shows Type text field instead of Name/Provider when type is changed to provider", async () => {
    const user = userEvent.setup();
    render(<DeployKeyForm notifyUser={() => {}} />);

    const typeSelect = screen.getByLabelText("Type");
    await user.click(typeSelect);
    const providerOption = await screen.findByRole("option", {
      name: "Provider",
    });
    await user.click(providerOption);

    expect(queryField("Name")).not.toBeInTheDocument();
    expect(queryField("Provider")).not.toBeInTheDocument();
    expect(getField("Type")).toBeInTheDocument();
  });

  it("calls notifyUser with 'success' on successful submit", async () => {
    const notifyUser = vi.fn();
    const user = userEvent.setup();
    render(<DeployKeyForm notifyUser={notifyUser} />);

    await user.type(
      getField("Source Repository URL"),
      "https://github.com/example/repo",
    );
    await user.type(getField("Namespace"), "my-namespace");
    await user.type(getField("Name"), "my-module");
    await user.type(getField("Provider"), "aws");

    await user.click(screen.getByRole("button", { name: "Create" }));

    await waitFor(() => {
      expect(notifyUser).toHaveBeenCalledWith(
        "success",
        "DeployKey for module created",
      );
    });
  });

  it("calls notifyUser with 'error' on failed submit", async () => {
    server.use(
      http.post("*/management/deploykey", () => {
        return HttpResponse.json(
          { error: "Internal Server Error" },
          { status: 500 },
        );
      }),
    );

    const notifyUser = vi.fn();
    const user = userEvent.setup();
    render(<DeployKeyForm notifyUser={notifyUser} />);

    await user.type(
      getField("Source Repository URL"),
      "https://github.com/example/repo",
    );
    await user.type(getField("Namespace"), "my-namespace");
    await user.type(getField("Name"), "my-module");
    await user.type(getField("Provider"), "aws");

    await user.click(screen.getByRole("button", { name: "Create" }));

    await waitFor(() => {
      expect(notifyUser).toHaveBeenCalledWith(
        "error",
        "DeployKey for module could not be created",
      );
    });
  });

  it("has no accessibility violations", async () => {
    const { container } = render(<DeployKeyForm notifyUser={() => {}} />);
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
