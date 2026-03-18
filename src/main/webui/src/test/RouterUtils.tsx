import React, { isValidElement } from "react";
import { render } from "@testing-library/react";
import { RouterProvider, createMemoryRouter } from "react-router";
import { UserProvider } from "../components/context/UserContext";
import { User } from "../types";
import { createUser } from "./mocks/fixtures";

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

export const renderWithProviders = (
  ui: React.ReactElement,
  {
    user = createUser(),
    route = "/",
    routes = [],
  }: {
    user?: User;
    route?: string;
    routes?: { path: string; element: React.ReactElement }[];
  } = {},
) => {
  const allRoutes = [{ path: route, element: ui }, ...routes];

  const router = createMemoryRouter(allRoutes, {
    initialEntries: [route],
    initialIndex: 0,
    future: {
      v7_partialHydration: true,
      v7_relativeSplatPath: true,
      v7_skipActionErrorRevalidation: true,
      v7_normalizeFormMethod: true,
      v7_fetcherPersist: true,
    },
  });

  return render(
    <UserProvider fetchedUser={user}>
      <RouterProvider router={router} future={{ v7_startTransition: true }} />
    </UserProvider>,
  );
};
