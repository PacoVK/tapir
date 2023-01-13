import React from "react";
import { Divider, Stack, Typography } from "@mui/material";
import { ModuleDependenciesProps } from "../../types";
import Container from "@mui/material/Container";

const ModuleDependencies = (props: ModuleDependenciesProps) => {
  const { modules, providers, ...other } = props;
  return (
    <>
      <Typography variant="h4">Module Dependencies</Typography>
      <Typography sx={{ mt: "1em" }}>Todo hint </Typography>
      <Stack
        justifyContent="center"
        alignItems="left"
        spacing={2}
        sx={{ mt: "2em", mb: "2em" }}
      >
        {modules ? (
          modules.map((module) => {
            return (
              <>
                <Typography>
                  <strong>{module.source}</strong>
                </Typography>
                <Container maxWidth="sm">
                  <Typography>
                    Version:{" "}
                    <em>{module.version ? module.version : "latest"}</em>
                  </Typography>
                </Container>
              </>
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

      <Typography sx={{ mt: "1em" }}>Todo hint </Typography>
      <Stack
        justifyContent="center"
        alignItems="left"
        spacing={2}
        sx={{ mt: "15px", mb: "15px" }}
      >
        {providers
          ? providers.map((provider) => {
              return (
                <>
                  <Typography>
                    <strong>{provider.name}</strong>
                  </Typography>
                  <Container maxWidth="sm">
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
                </>
              );
            })
          : null}
      </Stack>
    </>
  );
};

export default ModuleDependencies;
