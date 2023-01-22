import React from "react";
import { Box, Stack, Typography } from "@mui/material";
import { ModuleOutputProps } from "../../types";
import Container from "@mui/material/Container";

const ModuleOutput = (props: ModuleOutputProps) => {
  const { outputs } = props;
  return (
    <Stack justifyContent="center" alignItems="left" spacing={2}>
      {outputs && outputs.length > 0 ? (
        outputs.map((output, index) => {
          return (
            <Box key={`module-output-${index}`}>
              <Typography>
                <strong>{output.name}</strong>
              </Typography>
              <Container maxWidth="sm">
                <Typography>{output.description}</Typography>
              </Container>
            </Box>
          );
        })
      ) : (
        <Typography>This module has no outputs</Typography>
      )}
    </Stack>
  );
};

export default ModuleOutput;
