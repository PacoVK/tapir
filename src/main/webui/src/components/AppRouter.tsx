import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import ModuleOverview from "../views/ModuleOverview";
import ModuleDetails from "../views/ModuleDetails";
import ProviderOverview from "../views/ProviderOverview";
import ProviderDetails from "../views/ProviderDetails";

const AppRouter = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<ModuleOverview />} />
        <Route path="/providers" element={<ProviderOverview />} />
        <Route
          path="/module/:namespace/:name/:provider"
          loader={({ params }) => {
            return fetch(
              `http://localhost:8080/terraform/modules/v1/${params.namespace}/${params.name}/${params.provider}`
            );
          }}
          element={<ModuleDetails />}
        />
        <Route
          path="/providers/:namespace/:type"
          loader={({ params }) => {
            return fetch(
              `http://localhost:8080/terraform/providers/v1/${params.namespace}/${params.type}`
            );
          }}
          element={<ProviderDetails />}
        />
      </Routes>
    </Router>
  );
};

export default AppRouter;
