import React from 'react';
import {Box, Container, Typography} from "@mui/material";
import ModuleElement from "./components/list/ModuleElement";
import {Provider} from "./types";

function App() {
  const fet = async () => {
    const res = await fetch("http://localhost:8080/terraform/modules/v1");
      const respo = await res.json()
    console.log("Respo", respo)
  }
  return (
      <Container maxWidth={"xl"}>
          <Box margin={"5vh"}>
              <Typography
                  variant="h6"
                  noWrap
                  component="a"
                  href="/"
                  sx={{
                      mr: 2,
                      display: { xs: 'none', md: 'flex' },
                      fontFamily: 'monospace',
                      fontWeight: 700,
                      letterSpacing: '.3rem',
                      color: 'inherit',
                      textDecoration: 'none',
                  }}
              >
                  Private Terraform Registry
              </Typography>
          </Box>
          <Box>
              <ModuleElement module={{ name: "bloo", namespace: "barr", provider: Provider.AZURE}} />
              <ModuleElement module={{ name: "bloo", namespace: "barr", provider: Provider.AWS}} />
              <button onClick={fet}>tet</button>
          </Box>
      </Container>
  );
}

export default App;
