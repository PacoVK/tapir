import React, { useState } from "react";
import {
  Avatar,
  Chip,
  CircularProgress,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  Typography,
} from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";
import RocketLaunchIcon from "@mui/icons-material/RocketLaunch";
import { formatDate } from "../util/DateUtil";
import { useLocation, useParams } from "react-router-dom";
import BuildCircle from "@mui/icons-material/BuildCircle";

const ProviderDetails = () => {
  const routeParams = useParams();
  const location = useLocation();

  const [provider, setProvider] = useState(location.state?.data);
  const [version, setVersion] = React.useState();

  const fetchProvider = async () => {
    const baseUrl = window.location.href.replace(location.pathname, "");
    const response = await fetch(
      `${baseUrl}/terraform/providers/v1/${routeParams.namespace}/${routeParams.type}`
    );
    const provider = await response.json();
    setProvider(provider);
    // @ts-ignore
    setVersion(Object.keys(provider.versions)[0]);
  };

  const loadingRoutine = () => {
    fetchProvider();
    return <CircularProgress color={"secondary"} sx={{ margin: "auto" }} />;
  };

  const handleVersionChange = (event: SelectChangeEvent) => {
    // @ts-ignore
    setVersion(event.target.value);
  };

  return provider && version ? (
    <>
      <Stack direction="row" spacing={2} sx={{ mb: 2 }}>
        <Avatar alt={`${provider.namespace}${provider.type}`}>
          <BuildCircle />
        </Avatar>
        <Typography
          variant="h6"
          noWrap
          component="a"
          href="/providers"
          alignItems={"center"}
          sx={{
            mr: 2,
            display: { xs: "none", md: "flex" },
            fontFamily: "monospace",
            fontWeight: 700,
            letterSpacing: ".3rem",
            color: "inherit",
            textDecoration: "none",
          }}
        >
          {provider.namespace}/{provider.type}
        </Typography>
      </Stack>
      <Stack direction="row" spacing={2} sx={{ mb: 5 }} alignItems={"center"}>
        <FormControl>
          <InputLabel id="provider-version-select-label">Version</InputLabel>
          <Select
            labelId="provider-versions-select-label"
            id="provider-versions-select"
            value={version}
            label="Versions"
            onChange={handleVersionChange}
          >
            {Object.keys(provider.versions).map((version) => {
              return (
                <MenuItem key={version} value={version}>
                  {version}
                </MenuItem>
              );
            })}
          </Select>
        </FormControl>
        <Chip
          icon={<InfoIcon />}
          label={`Latest version: ${Object.keys(provider.versions)[0]}`}
        />
        <Chip
          icon={<RocketLaunchIcon />}
          label={`Last published at: ${formatDate(provider.published_at)}`}
        />
      </Stack>
    </>
  ) : (
    loadingRoutine()
  );
};

export default ProviderDetails;
