import * as React from "react";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import Link from "@mui/material/Link";

const SubInfo = () => {
  return (
    <Typography variant="body2" color="text.secondary">
      {"Crafted with "}&#128525;{", Hosted on "}
      <Link color="inherit" href="https://github.com/PacoVK/tapir">
        GitHub
      </Link>{" "}
      {new Date().getFullYear()}
      {"."}
    </Typography>
  );
};

const Footer = () => {
  return (
    <Box
      mt={"15vh"}
      alignItems={"center"}
      sx={{
        display: "flex",
        flexDirection: "column",
      }}
    >
      <Box
        component="footer"
        sx={{
          py: 3,
          px: 2,
          mt: "auto",
        }}
      >
        <Container maxWidth="sm">
          <Typography variant="body1">
            Terraform Private Registry v{process.env.REACT_APP_VERSION}
          </Typography>
          <SubInfo />
        </Container>
      </Box>
    </Box>
  );
};

export default Footer;
