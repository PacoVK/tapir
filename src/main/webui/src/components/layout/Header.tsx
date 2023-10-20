import React from "react";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import { useUserContext } from "../context/UserContext";
import { Avatar } from "@mui/material";
import { deepOrange } from "@mui/material/colors";

const Header = () => {
  const { user } = useUserContext();
  return (
    <AppBar position="static" sx={{ backgroundColor: "#474747" }}>
      <Box alignSelf={"center"}>
        <Toolbar disableGutters>
          <Typography
            variant="h6"
            noWrap
            component="a"
            href="/"
            sx={{
              display: "flex",
              fontFamily: "monospace",
              fontWeight: 700,
              letterSpacing: ".3rem",
              color: "inherit",
              textDecoration: "none",
            }}
          >
            Tapir - Private Terraform Registry
          </Typography>
        </Toolbar>
      </Box>
    </AppBar>
  );
};

export default Header;

/*
<Avatar
              sx={{ bgcolor: deepOrange[500] }}
          >{user.name ? user.name.charAt(0).toUpperCase() : undefined}</Avatar>
 */
