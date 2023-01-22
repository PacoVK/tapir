import React from "react";
import { Box, Divider, Stack, Typography } from "@mui/material";
import { ModuleDependenciesProps } from "../../types";
import Container from "@mui/material/Container";

const ModuleDependencies = (props: ModuleDependenciesProps) => {
  const { modules, providers } = props;
  return (
    <>
      <Typography variant="h4">Module Dependencies</Typography>
      <Typography sx={{ mt: "1em" }}>
        {" "}
        Dependencies are external modules that this module references. A module
        is considered external if it isn't within the same repository.
      </Typography>
      <Stack
        justifyContent="center"
        alignItems="left"
        spacing={2}
        sx={{ mt: "2em", mb: "2em" }}
      >
        {modules ? (
          modules.map((module, index) => {
            return (
              <Box key={`module-dependency-module-${index}`}>
                <Typography>
                  <strong>{module.source}</strong>
                </Typography>
                <Container sx={{ ml: "inherit" }} maxWidth="sm">
                  <Typography>
                    Version:{" "}
                    <em>{module.version ? module.version : "latest"}</em>
                  </Typography>
                </Container>
              </Box>
            );
          })
        ) : (
          <Typography variant="h6">
            This module has no external module dependencies
          </Typography>
        )}
      </Stack>
      <Divider sx={{ mb: "2em" }} />
      <Typography variant="h4">Provider Dependencies</Typography>
      <Typography sx={{ mt: "1em" }}>
        Providers are Terraform plugins that will be automatically installed, if
        available on the Terraform Registry.
      </Typography>
      <Stack
        justifyContent="center"
        alignItems="left"
        spacing={2}
        sx={{ mt: "15px", mb: "15px" }}
      >
        {providers
          ? providers.map((provider, index) => {
              return (
                <Box key={`module-dependency-provider-${index}`}>
                  <Typography>
                    <strong>{provider.name}</strong>
                  </Typography>
                  <Container sx={{ ml: "inherit" }} maxWidth="sm">
                    {provider.alias ? (
                      <Typography>
                        Alias: <em>{provider.alias}</em>
                      </Typography>
                    ) : null}
                    <Typography>
                      Version:{" "}
                      <em>{provider.version ? provider.version : "latest"}</em>
                    </Typography>
                  </Container>
                </Box>
              );
            })
          : null}
      </Stack>
    </>
  );
};

export default ModuleDependencies;
