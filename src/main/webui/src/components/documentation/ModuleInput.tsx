import React from "react";
import { Badge, Container, Stack, Typography } from "@mui/material";
import { ModuleInputProps } from "../../types";
import SyntaxHighlighter from "react-syntax-highlighter/dist/cjs/light";
import { ghcolors as theme } from "react-syntax-highlighter/dist/esm/styles/prism";

const ModuleInput = (props: ModuleInputProps) => {
  const { inputs } = props;
  return (
    <Stack justifyContent="center" alignItems="left" spacing={2}>
      {inputs && inputs.length > 0 ? (
        inputs.map((input) => {
          return (
            <>
              <Typography>
                <Badge
                  color="secondary"
                  variant="dot"
                  invisible={!input.required}
                >
                  <strong>{input.name}</strong>
                </Badge>
              </Typography>
              <Container maxWidth="sm">
                <Typography>
                  {input.description ? input.description : null}
                </Typography>
                <SyntaxHighlighter language="hcl" style={theme}>
                  {input.type}
                </SyntaxHighlighter>
              </Container>
            </>
          );
        })
      ) : (
        <Typography>This module has no inputs</Typography>
      )}
    </Stack>
  );
};

export default ModuleInput;
