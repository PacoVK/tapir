import React from "react";
import {
  Divider,
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar
} from "@mui/material";
import ViewModuleIcon from "@mui/icons-material/ViewModule";
import AppsIcon from "@mui/icons-material/Apps";
import KeyIcon from "@mui/icons-material/Key";
import { useUserContext } from "../context/UserContext";
import { Link } from "react-router-dom";

const NavigationDrawer = () => {
  const { isAdmin } = useUserContext();
  return (
    <Drawer
      sx={{
        width: 240,
        flexShrink: 0,
        "& .MuiDivider-root": {
          display: "none"
        },
        "& .MuiDrawer-paper": {
          width: 240,
          boxSizing: "border-box",
          backgroundColor: "transparent",
          border: "none"
        }
      }}
      variant="permanent"
      anchor="left"
    >
      <Toolbar disableGutters sx={{}} />
      <Divider />
      <List>
        <ListItem
          key={"MenuItemModule"}
          component={Link}
          to={"/"}
          disablePadding
        >
          <ListItemButton>
            <ListItemIcon>
              <ViewModuleIcon />
            </ListItemIcon>
            <ListItemText primary={"Modules"} />
          </ListItemButton>
        </ListItem>
        <ListItem
          key={"MenuItemProvider"}
          component={Link}
          to={"/providers"}
          disablePadding
        >
          <ListItemButton>
            <ListItemIcon>
              <AppsIcon />
            </ListItemIcon>
            <ListItemText primary={"Providers"} />
          </ListItemButton>
        </ListItem>
        {isAdmin ? (
          <ListItem
            key={"MenuItemManagement"}
            component={Link}
            to={"/management"}
            disablePadding
          >
            <ListItemButton>
              <ListItemIcon>
                <KeyIcon />
              </ListItemIcon>
              <ListItemText primary={"Management"} />
            </ListItemButton>
          </ListItem>
        ) : null}
      </List>
    </Drawer>
  );
};
export default NavigationDrawer;
