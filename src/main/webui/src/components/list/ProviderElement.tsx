import React from "react";
import { Avatar, Chip, Grid, Paper, Stack, Typography } from "@mui/material";
import BuildCircle from "@mui/icons-material/BuildCircle";
import RocketLaunchIcon from "@mui/icons-material/RocketLaunch";
import { formatDate } from "../../util/DateUtil";
import InfoIcon from "@mui/icons-material/Info";
import { Provider } from "../../types";
import { Link } from "react-router-dom";

const ProviderElement = ({ provider }: { provider: Provider }) => {
  return (
    <Grid
      item
      xs={6}
      key={`${provider.namespace}${provider.type}`}
      component={Link}
      to={`${provider.namespace}/${provider.type}`}
      state={{ data: provider }}
    >
      <Paper sx={{ p: "0.5em" }}>
        <Stack alignItems={"center"}>
          <Avatar alt={`${provider.namespace}${provider.type}`}>
            <BuildCircle />
          </Avatar>
          <Typography variant={"h5"}>
            {provider.namespace}-{provider.type}
          </Typography>
        </Stack>
        <Stack
          spacing={2}
          direction={{ xs: "column", lg: "row" }}
          justifyContent={"center"}
          sx={{ mt: "0.5em" }}
        >
          <Chip
            icon={<InfoIcon />}
            label={`Latest version: ${Object.keys(provider.versions)[0]}`}
          />
          <Chip
            icon={<RocketLaunchIcon />}
            label={`Last published at: ${formatDate(provider.published_at)}`}
          />
        </Stack>
      </Paper>
    </Grid>
  );
};

export default ProviderElement;
