import React from "react";
import { Badge, Box, Container, Stack, Typography } from "@mui/material";
import { ModuleInputProps } from "../../types";
import SyntaxHighlighter from "react-syntax-highlighter/dist/cjs/light";
import { ghcolors as theme } from "react-syntax-highlighter/dist/esm/styles/prism";

const ModuleInput = (props: ModuleInputProps) => {
  const { inputs } = props;

  const renderDefaultValue = (defaultValue: any) => {
    if (typeof defaultValue === "object") {
      return JSON.stringify(defaultValue);
    } else {
      return defaultValue;
    }
  };

  return (
    <Stack justifyContent="center" alignItems="left" spacing={2}>
      {inputs && inputs.length > 0 ? (
        inputs.map((input, index) => {
          return (
            <Box key={`module-input-${index}`}>
              <Typography>
                <Badge
                  color="secondary"
                  variant="dot"
                  invisible={!input.required}
                >
                  <strong>{input.name}</strong>
                </Badge>
              </Typography>
              <Container sx={{ ml: "inherit" }} maxWidth="sm">
                <Typography>
                  {input.description ? input.description : null}
                </Typography>
                <SyntaxHighlighter language="hcl" style={theme}>
                  {`${input.type} ${
                    input.default
                      ? `= ${renderDefaultValue(input.default)}`
                      : ""
                  }`}
                </SyntaxHighlighter>
              </Container>
            </Box>
          );
        })
      ) : (
        <Typography>This module has no inputs</Typography>
      )}
    </Stack>
  );
};

export default ModuleInput;
