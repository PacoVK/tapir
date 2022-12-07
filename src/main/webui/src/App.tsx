import React, {useEffect, useState} from 'react';
import {Box, Container, Typography} from "@mui/material";
import ModuleElement from "./components/list/ModuleElement";
import {Module} from "./types";

const  App = () => {

    const [modules, setModules] = useState([] as Module[]);

    useEffect(() => {
        fetchModules();
    }, []);

  const fetchModules = async () => {
    const response = await fetch("http://localhost:8080/terraform/modules/v1");
    const modules = await response.json();
    setModules(modules);
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
              {modules.map(module =>
                  <ModuleElement key={`${module.name}${module.provider}`} module={module} />
              )}
          </Box>
      </Container>
  );
}

export default App;
