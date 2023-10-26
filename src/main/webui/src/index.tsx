import React from "react";
import ReactDOM from "react-dom/client";

import "@fontsource/roboto/300.css";
import "@fontsource/roboto/400.css";
import "@fontsource/roboto/500.css";
import "@fontsource/roboto/700.css";
import Header from "./components/layout/Header";
import Footer from "./components/layout/Footer";
import AppRouter from "./components/AppRouter";
import CssBaseline from "@mui/material/CssBaseline";
import { Box, Stack } from "@mui/material";
import NavigationDrawer from "./components/nav/NavigationDrawer";
import { UserProvider } from "./components/context/UserContext";

const fetchUser = async () => {
  const response = await fetch("/tapir/user");
  return await response.json();
};

const user = await fetchUser();

const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement,
);
root.render(
  <React.StrictMode>
    <CssBaseline />
    <UserProvider fetchedUser={user}>
      <Header />
      <Box sx={{ display: "flex" }}>
        <NavigationDrawer />
        <Stack spacing={2} m={"auto"} mt={"5vh"} sx={{ width: "75%" }}>
          <AppRouter />
        </Stack>
      </Box>
      <Footer />
    </UserProvider>
  </React.StrictMode>,
);
