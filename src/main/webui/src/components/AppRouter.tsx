import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Overview from "../views/Overview";
import ModuleDetails from "../views/ModuleDetails";

const AppRouter = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Overview />} />
        <Route
          path="/module/:namespace/:name/:provider"
          loader={({ params }) => {
            return fetch(
              `http://localhost:8080/terraform/modules/v1/${params.namespace}/${params.name}/${params.provider}`
            );
          }}
          element={<ModuleDetails />}
        />
      </Routes>
    </Router>
  );
};

export default AppRouter;
