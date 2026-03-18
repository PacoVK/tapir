import { render, screen } from "@testing-library/react";
import { axe } from "jest-axe";
import { RouterProvider, createMemoryRouter } from "react-router-dom";
import ProviderDetails from "./ProviderDetails";

const renderProviderDetails = () => {
  const router = createMemoryRouter(
    [
      {
        path: "/providers/:namespace/:type",
        element: <ProviderDetails />,
      },
      { path: "/404", element: <div>Not Found</div> },
    ],
    {
      initialEntries: ["/providers/hashicorp/aws"],
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

describe("<ProviderDetails />", () => {
  it("renders provider name after data loads", async () => {
    renderProviderDetails();
    expect(await screen.findByText("hashicorp/aws")).toBeInTheDocument();
  });

  it("renders Usage heading", async () => {
    renderProviderDetails();
    await screen.findByText("hashicorp/aws");
    expect(screen.getByText("Usage")).toBeInTheDocument();
  });

  it("renders important links", async () => {
    renderProviderDetails();
    await screen.findByText("hashicorp/aws");
    expect(screen.getByText("Important Links")).toBeInTheDocument();
    expect(screen.getByText("Using providers")).toBeInTheDocument();
    expect(screen.getByText("HashiCorp Tutorials")).toBeInTheDocument();
  });

  it("has no accessibility violations", async () => {
    const { container } = renderProviderDetails();
    await screen.findByText("hashicorp/aws");
    const results = await axe(container, {
      rules: {
        // MUI Select has label association issues (labelId mismatch in source)
        "aria-input-field-name": { enabled: false },
        // MUI List renders li items outside proper ul context
        listitem: { enabled: false },
      },
    });
    expect(results).toHaveNoViolations();
  });
});
