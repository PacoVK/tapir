import React from "react";
import { Box, Chip, Paper, Stack } from "@mui/material";
import { Module } from "../../types";
import Typography from "@mui/material/Typography";
import DownloadIcon from "@mui/icons-material/Download";
import RocketLaunchIcon from "@mui/icons-material/RocketLaunch";
import InfoIcon from "@mui/icons-material/Info";
import { getProviderLogo } from "../../util/LogoUtil";
import { formatDate } from "../../util/DateUtil";
import { Link } from "react-router-dom";

const ModuleElement = ({ module }: { module: Module }) => {
  return (
    <Box
      marginY={2}
      component={Link}
      to={`module/${module.namespace}/${module.name}/${module.provider}`}
      state={{ data: module }}
    >
      <Paper>
        <Stack direction={"row"} spacing={4} alignItems={"center"}>
          <img
            style={{
              marginTop: 10,
              marginBottom: 10,
              marginLeft: 10,
              width: 64,
              height: 64,
            }}
            alt={"Provider logo"}
            src={getProviderLogo(module.provider)}
          />
          <Box>
            <Typography variant="h5" marginY={2}>
              {module.namespace}/{module.name}
            </Typography>
          </Box>
        </Stack>
        <Stack direction={"row"} spacing={2} ml={2}>
          <Chip
            icon={<DownloadIcon />}
            label={`Total downloads: ${module.downloads}`}
          />
          <Chip
            icon={<InfoIcon />}
            label={`Latest version: ${
              module.versions.at(module.versions.length - 1)!.version
            }`}
          />
          <Chip
            icon={<RocketLaunchIcon />}
            label={`Last published at: ${formatDate(module.published_at)}`}
          />
        </Stack>
      </Paper>
    </Box>
  );
};

export default ModuleElement;
