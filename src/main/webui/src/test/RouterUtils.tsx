import React, { isValidElement } from "react";
import { render } from "@testing-library/react";
import { RouterProvider, createMemoryRouter } from "react-router-dom";

export const renderWithRouter = (children: any, routes = []) => {
  const options = isValidElement(children)
    ? { element: children, path: "/" }
    : children;

  const router = createMemoryRouter([{ ...options }, ...routes], {
    initialEntries: [options.path],
    initialIndex: 1,
    future: {
      v7_partialHydration: true,
      v7_relativeSplatPath: true,
      v7_skipActionErrorRevalidation: true,
      v7_normalizeFormMethod: true,
      v7_fetcherPersist: true,
    },
  });

  return render(
    <RouterProvider router={router} future={{ v7_startTransition: true }} />,
  );
};
