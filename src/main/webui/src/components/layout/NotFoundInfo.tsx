import React from "react";
import tapirLogo from "../../assets/tapir.png";
import { Box, Typography } from "@mui/material";

const NotFoundInfo = ({ entity }: { entity: string }) => {
  return (
    <Box sx={{ margin: "auto" }}>
      <img alt={"Tapir logo"} src={tapirLogo} />
      <Typography textAlign={"center"}>
        Found this Tapir, but no {entity}
      </Typography>
    </Box>
  );
};

export default NotFoundInfo;
