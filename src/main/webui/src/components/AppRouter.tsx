import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import ModuleOverview from "../views/ModuleOverview";
import ModuleDetails from "../views/ModuleDetails";
import ProviderOverview from "../views/ProviderOverview";
import ProviderDetails from "../views/ProviderDetails";
import ManagementView from "../views/ManagementView";
import { useUserContext } from "./context/UserContext";
import NotFoundPage from "../views/404";

const AppRouter = () => {
  const { isAdmin } = useUserContext();
  return (
      <Routes>
        <Route path="/" element={<ModuleOverview />} />
        <Route path="/providers" element={<ProviderOverview />} />
        {isAdmin ? (
          <Route path="/management" element={<ManagementView />} />
        ) : null}
        <Route
          path="/module/:namespace/:name/:provider"
          loader={({ params }) => {
            return fetch(
              `http://localhost:8080/terraform/modules/v1/${params.namespace}/${params.name}/${params.provider}`,
            );
          }}
          element={<ModuleDetails />}
        />
        <Route
          path="/providers/:namespace/:type"
          loader={({ params }) => {
            return fetch(
              `http://localhost:8080/terraform/providers/v1/${params.namespace}/${params.type}`,
            );
          }}
          element={<ProviderDetails />}
        />
        <Route path={"*"} element={<NotFoundPage />} />
      </Routes>
  );
};

export default AppRouter;
