import React, { useEffect, useState } from "react";
import {
  Box,
  Container,
  FormControl,
  InputLabel,
  MenuItem,
  Pagination,
  Select,
  SelectChangeEvent,
  Stack,
  Typography,
} from "@mui/material";
import ModuleElement from "../components/list/ModuleElement";
import { Module } from "../types";

const Overview = () => {
  const [modules, setModules] = useState([] as Module[]);
  const [page, setPage] = React.useState(1);
  const [totalModulesCount, setTotalModulesCount] = React.useState(0);
  const [resultPerPage, setResultPerPage] = React.useState("10");
  const handleChange = (event: React.ChangeEvent<unknown>, value: number) => {
    setPage(value);
  };

  useEffect(() => {
    fetchModules();
  }, [page, resultPerPage]);

  const handleResultPerPageChange = (event: SelectChangeEvent) => {
    // @ts-ignore
    setResultPerPage(event.target.value);
    setPage(1);
  };

  const fetchModules = async () => {
    const response = await fetch(
      `terraform/modules/v1?offset=${
        parseInt(resultPerPage) * (page - 1)
      }&limit=${resultPerPage}`
    );
    const result = await response.json();
    setModules(result.modules);
    setTotalModulesCount(result.totalModulesCount);
  };

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
            display: { xs: "none", md: "flex" },
            fontFamily: "monospace",
            fontWeight: 700,
            letterSpacing: ".3rem",
            color: "inherit",
            textDecoration: "none",
          }}
        >
          Private Terraform Registry
        </Typography>
      </Box>
      <Stack spacing={2}>
        <Stack spacing={4} direction={"row"} alignItems={"center"}>
          <FormControl>
            <InputLabel id="module-version-select-label">
              Results per page
            </InputLabel>
            <Select
              labelId="module-versions-select-label"
              id="module-versions-select"
              value={resultPerPage}
              label="Versions"
              onChange={handleResultPerPageChange}
            >
              <MenuItem key={"page-2"} value={"2"}>
                2
              </MenuItem>
              <MenuItem key={"page-10"} value={"10"}>
                10
              </MenuItem>
              <MenuItem key={"page-20"} value={"20"}>
                20
              </MenuItem>
            </Select>
          </FormControl>
          <Pagination
            key={"pagination-top"}
            count={Math.ceil(totalModulesCount / parseInt(resultPerPage))}
            page={page}
            onChange={handleChange}
          />
        </Stack>
        {modules.map((module) => (
          <ModuleElement
            key={`${module.namespace}${module.name}${module.provider}`}
            module={module}
          />
        ))}
        <Pagination
          key={"pagination-bottom"}
          count={Math.ceil(totalModulesCount / parseInt(resultPerPage))}
          page={page}
          onChange={handleChange}
        />
      </Stack>
    </Container>
  );
};

export default Overview;
