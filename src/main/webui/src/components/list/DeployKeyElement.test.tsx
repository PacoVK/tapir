import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { axe } from "jest-axe";
import DeployKeyElement from "./DeployKeyElement";
import { createDeployKey } from "../../test/mocks/fixtures";

const writeTextMock = vi.fn().mockResolvedValue(undefined);

describe("<DeployKeyElement />", () => {
  const defaultProps = () => ({
    deployKey: createDeployKey(),
    onCopy: vi.fn(),
    onRegenerate: vi.fn(),
    onDelete: vi.fn(),
  });

  beforeEach(() => {
    Object.defineProperty(navigator, "clipboard", {
      value: { writeText: writeTextMock },
      writable: true,
      configurable: true,
    });
    writeTextMock.mockClear();
  });

  it("renders deploy key ID as primary text", () => {
    render(<DeployKeyElement {...defaultProps()} />);
    expect(screen.getByText("dk-test-key-001")).toBeInTheDocument();
  });

  it("renders deploy key value", () => {
    render(<DeployKeyElement {...defaultProps()} />);
    expect(screen.getByText("sdlshdfkjlf-fake-key-value")).toBeInTheDocument();
  });

  it("renders formatted last modified date", () => {
    render(<DeployKeyElement {...defaultProps()} />);
    expect(
      screen.getByText(/Last modified at: 10-03-2024/),
    ).toBeInTheDocument();
  });

  it("copy button calls onCopy callback", async () => {
    const user = userEvent.setup();
    const props = defaultProps();
    render(<DeployKeyElement {...props} />);

    const copyButton = screen.getByRole("button", { name: "copy" });
    await user.click(copyButton);

    await waitFor(() => {
      expect(props.onCopy).toHaveBeenCalledWith("dk-test-key-001");
    });
  });

  it("delete button opens confirmation dialog", async () => {
    const user = userEvent.setup();
    render(<DeployKeyElement {...defaultProps()} />);

    const deleteButton = screen.getByRole("button", { name: "delete" });
    await user.click(deleteButton);

    expect(
      await screen.findByText(/Delete Deploykey dk-test-key-001/),
    ).toBeInTheDocument();
    expect(
      screen.getByText(/Are you sure you want to delete dk-test-key-001/),
    ).toBeInTheDocument();
  });

  it("confirming delete dialog calls onDelete with key ID", async () => {
    const user = userEvent.setup();
    const props = defaultProps();
    render(<DeployKeyElement {...props} />);

    await user.click(screen.getByRole("button", { name: "delete" }));
    await screen.findByText("Confirm");
    await user.click(screen.getByText("Confirm"));

    await waitFor(() => {
      expect(props.onDelete).toHaveBeenCalledWith("dk-test-key-001");
    });
  });

  it("canceling delete dialog closes it without calling onDelete", async () => {
    const user = userEvent.setup();
    const props = defaultProps();
    render(<DeployKeyElement {...props} />);

    await user.click(screen.getByRole("button", { name: "delete" }));
    await screen.findByText("Cancel");
    await user.click(screen.getByText("Cancel"));

    await waitFor(() => {
      expect(
        screen.queryByText(/Delete Deploykey dk-test-key-001/),
      ).not.toBeInTheDocument();
    });
    expect(props.onDelete).not.toHaveBeenCalled();
  });

  it("regenerate button opens confirmation dialog", async () => {
    const user = userEvent.setup();
    render(<DeployKeyElement {...defaultProps()} />);

    const regenerateButton = screen.getByRole("button", {
      name: "regenerate",
    });
    await user.click(regenerateButton);

    expect(
      await screen.findByText(/Regenerate Deploykey for dk-test-key-001/),
    ).toBeInTheDocument();
  });

  it("confirming regenerate calls onRegenerate with key ID", async () => {
    const user = userEvent.setup();
    const props = defaultProps();
    render(<DeployKeyElement {...props} />);

    await user.click(screen.getByRole("button", { name: "regenerate" }));
    await screen.findByText("Confirm");
    await user.click(screen.getByText("Confirm"));

    await waitFor(() => {
      expect(props.onRegenerate).toHaveBeenCalledWith("dk-test-key-001");
    });
  });

  it("has no accessibility violations", async () => {
    const { container } = render(<DeployKeyElement {...defaultProps()} />);
    const results = await axe(container, {
      rules: {
        // MUI ListItem renders <li> outside of <ul> context in isolation
        listitem: { enabled: false },
      },
    });
    expect(results).toHaveNoViolations();
  });
});
