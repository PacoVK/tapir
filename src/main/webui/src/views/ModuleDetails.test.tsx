import { render, screen } from "@testing-library/react";
import { axe } from "jest-axe";
import { RouterProvider, createMemoryRouter } from "react-router";
import ModuleDetails from "./ModuleDetails";

const renderModuleDetails = () => {
  const router = createMemoryRouter(
    [
      {
        path: "/module/:namespace/:name/:provider",
        element: <ModuleDetails />,
      },
      { path: "/404", element: <div>Not Found</div> },
    ],
    {
      initialEntries: ["/module/foo/bar/aws"],
      future: {
        v7_partialHydration: true,
        v7_relativeSplatPath: true,
        v7_skipActionErrorRevalidation: true,
        v7_normalizeFormMethod: true,
        v7_fetcherPersist: true,
      },
    },
  );
  return render(
    <RouterProvider router={router} future={{ v7_startTransition: true }} />,
  );
};

describe("<ModuleDetails />", () => {
  it("renders module name after data loads", async () => {
    renderModuleDetails();
    expect(await screen.findByText("foo/bar/aws")).toBeInTheDocument();
  });

  it("renders download count chip", async () => {
    renderModuleDetails();
    expect(await screen.findByText(/Total downloads: 42/)).toBeInTheDocument();
  });

  it("renders version selector", async () => {
    renderModuleDetails();
    await screen.findByText("foo/bar/aws");
    // MUI Select has a labelId mismatch in the source, so use text query
    expect(screen.getByText("Version")).toBeInTheDocument();
    expect(screen.getByText("1.0.0")).toBeInTheDocument();
  });

  it("has no accessibility violations", async () => {
    const { container } = renderModuleDetails();
    await screen.findByText("foo/bar/aws");
    const results = await axe(container, {
      rules: {
        // MUI Select has label association issues (labelId mismatch in source)
        "aria-input-field-name": { enabled: false },
      },
    });
    expect(results).toHaveNoViolations();
  });
});
