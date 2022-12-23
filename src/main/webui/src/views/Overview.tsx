import React, {
  useCallback,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from "react";
import {
  Box,
  Container,
  List,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import ModuleElement from "../components/list/ModuleElement";
import { Module } from "../types";
import useDebounce from "../hooks/useDebounce";

const fetchDataLimit = 5;
const Overview = () => {
  const modulesTable = useRef();
  const [modules, setModules] = useState([] as Module[]);
  const [lastEvaluatedKey, setLastEvaluatedKey] = useState("");
  const [searchString, setSearchString] = useState("");
  const debouncedSearchTerm = useDebounce(searchString, 500);

  const [loading, setLoading] = useState(false);
  const [distanceBottom, setDistanceBottom] = useState(0);

  const loadMore = useCallback(() => {
    setLoading(true);
    fetchModules(
      `terraform/modules/v1?limit=${fetchDataLimit}&lastKey=${lastEvaluatedKey}&q=${searchString}`
    ).then((data) => {
      // TODO handle no more modules available
      const allModules = [...modules, ...data.modules];
      setLastEvaluatedKey(allModules.at(allModules.length - 1).id);
      setModules(allModules);
      setLoading(false);
    });
  }, [modules]);

  const scrollListener = useCallback(() => {
    let bottom =
      // @ts-ignore
      modulesTable.current.scrollHeight - modulesTable.current.clientHeight;

    if (!distanceBottom) {
      setDistanceBottom(Math.round(bottom * 0.2));
    }
    // @ts-ignore
    if (modulesTable.current.scrollTop > bottom - distanceBottom && !loading) {
      loadMore();
    }
  }, [loadMore, loading, distanceBottom]);

  useLayoutEffect(() => {
    const tableRef = modulesTable.current;
    // @ts-ignore
    tableRef.addEventListener("scroll", scrollListener);
    return () => {
      // @ts-ignore
      tableRef.removeEventListener("scroll", scrollListener);
    };
  }, [scrollListener]);

  useEffect(() => {
    setLoading(true);
    fetchModules(
      `terraform/modules/v1?limit=${fetchDataLimit}&q=${searchString}`
    ).then((data) => {
      setLastEvaluatedKey(data.modules.at(data.modules.length - 1).id);
      setModules(data.modules);
      setLoading(false);
    });
  }, [debouncedSearchTerm]);

  const fetchModules = async (api: string) => {
    const response = await fetch(api);
    return await response.json();
  };

  const handleSearchInputChange = (event: {
    target: { value: React.SetStateAction<string> };
  }) => {
    setSearchString(event.target.value);
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
        <TextField
          id="module-search"
          label="Search module"
          type="search"
          value={searchString}
          onChange={handleSearchInputChange}
        />
        <List
          component={Stack}
          sx={{ maxHeight: "50vh", overflow: "auto" }}
          // @ts-ignore
          ref={modulesTable}
        >
          {modules
            ? modules.map((module) => (
                <ModuleElement
                  key={`${module.namespace}${module.name}${module.provider}`}
                  module={module}
                />
                //TODO no modules found
              ))
            : null}
        </List>
      </Stack>
    </Container>
  );
};

export default Overview;
