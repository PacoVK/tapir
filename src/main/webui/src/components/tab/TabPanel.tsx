import React from "react";
import { Box } from "@mui/material";
import { TabPanelProps } from "../../types";

const TabPanel = (props: TabPanelProps) => {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`module-tabpanel-${index}`}
      aria-labelledby={`module-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ mt: "1em" }}>{children}</Box>}
    </div>
  );
};

export default TabPanel;
