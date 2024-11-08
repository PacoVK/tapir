import React from "react";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import { useUserContext } from "../context/UserContext";
import { Avatar, Grid2 as Grid, Button, Menu, MenuItem } from "@mui/material";
import { deepOrange, orange } from "@mui/material/colors";
import LaunchIcon from "@mui/icons-material/Launch";

const Header = () => {
  const { user } = useUserContext();
  const [menuAchor, setMenuAchor] = React.useState<null | HTMLElement>(null);
  const open = Boolean(menuAchor);
  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setMenuAchor(event.currentTarget);
  };
  const handleClose = () => {
    setMenuAchor(null);
  };

  const openTapirInNewTab = () => {
    handleClose();
    window.open(
      process.env.REACT_APP_TAPIR_DOCS,
      "_blank",
      "noopener,noreferrer",
    );
  };

  const logout = async () => {
    handleClose();
    await fetch("logout").finally(() => window.location.replace("/"));
  };
  return (
    <AppBar position="static" sx={{ backgroundColor: "#474747" }}>
      <Grid container spacing={3} sx={{ flexGrow: 1 }}>
        <Grid size={{ xs: 4 }} offset={{ md: "auto" }}></Grid>
        <Grid size={{ xs: 4 }} offset={{ md: 0, xs: 0 }} alignSelf={"center"}>
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
                margin: "auto",
                overflow: "visible",
              }}
            >
              Tapir - Private Terraform Registry
            </Typography>
          </Toolbar>
        </Grid>
        <Grid size={{ xs: 4 }} alignSelf={"center"}>
          <Avatar
            onClick={handleClick}
            component={Button}
            id="menu-button"
            aria-controls={open ? "menu-menu" : undefined}
            aria-haspopup="true"
            aria-expanded={open ? "true" : undefined}
            sx={{
              ":hover": {
                cursor: "pointer",
                bgcolor: deepOrange[500],
              },
              bgcolor: orange[500],
              float: "right",
              marginRight: "2vw",
              minWidth: "unset",
            }}
          >
            {user.name ? user.name.charAt(0).toUpperCase() : undefined}
          </Avatar>
          <Menu
            id="user-menu"
            anchorEl={menuAchor}
            open={open}
            onClose={handleClose}
            MenuListProps={{
              "aria-labelledby": "menu-button",
            }}
          >
            <MenuItem onClick={openTapirInNewTab}>
              About
              <LaunchIcon sx={{ ml: "5px" }} />
            </MenuItem>
            <MenuItem onClick={logout}>Logout</MenuItem>
          </Menu>
        </Grid>
      </Grid>
    </AppBar>
  );
};

export default Header;

/*

 */
