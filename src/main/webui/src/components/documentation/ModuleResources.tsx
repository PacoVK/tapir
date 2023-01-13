import React from "react";
import { Chip, Stack, Typography } from "@mui/material";
import { ModuleResourcesProps } from "../../types";

const ModuleResources = (props: ModuleResourcesProps) => {
  const { resources, ...other } = props;
  return (
    <>
      <Typography variant="h4">Resources</Typography>
      <Stack justifyContent="center" alignItems="left" spacing={2}>
        <Typography sx={{ mt: "1em" }} width={"50%"}>
          This is the list of resources that the module may create. The module
          can create zero or more of each of these resources depending on the
          count value. The count value is determined at runtime. The goal of
          this page is to present the types of resources that may be created.
        </Typography>
        <Typography width={"50%"}>
          This list contains all the resources this plus any submodules may
          create. When using this module, it may create fewer resources if you
          use a submodule.
        </Typography>
        <Typography>
          This module defines <strong>{resources.length}</strong> resources.
        </Typography>
        <ul>
          {resources.map((resource) => {
            return (
              <>
                <li>
                  <Chip
                    sx={{ m: "0.5em" }}
                    label={`${resource.type}.${resource.name}`}
                    variant="outlined"
                  />
                </li>
              </>
            );
          })}
        </ul>
      </Stack>
    </>
  );
};

export default ModuleResources;
