import React, {
  useCallback,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from "react";
import {
  Box,
  CircularProgress,
  List,
  Paper,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import ModuleElement from "../components/list/ModuleElement";
import { Module } from "../types";
import useDebounce from "../hooks/useDebounce";
import tapirLogo from "../assets/tapir.png";

const fetchDataLimit = 5;
const Overview = () => {
  const modulesTable = useRef();
  const [modules, setModules] = useState([] as Module[]);
  const [lastEvaluatedItemKey, setLastEvaluatedItemKey] = useState("");
  const [searchString, setSearchString] = useState("");
  const debouncedSearchTerm = useDebounce(searchString, 500);

  const [loading, setLoading] = useState(false);
  const [distanceBottom, setDistanceBottom] = useState(0);

  const hasMoreData = (lastEvaluatedItem: any) => {
    return (
      !!lastEvaluatedItem &&
      lastEvaluatedItem !== "" &&
      modules.at(0)?.id !== lastEvaluatedItem.id
    );
  };

  const loadMore = useCallback(
    () => {
      setLoading(true);
      fetchModules(
        `search?limit=${fetchDataLimit}&lastKey=${lastEvaluatedItemKey}&q=${searchString}`
      ).then((data) => {
        const allModules = [...modules, ...data.modules];
        setLastEvaluatedItemKey(
          data.lastEvaluatedItem ? data.lastEvaluatedItem.id : ""
        );
        setModules(allModules);
        setLoading(false);
      });
    },
    // eslint-disable-next-line
    [modules]
  );

  const scrollListener = useCallback(
    () => {
      let bottom =
        // @ts-ignore
        modulesTable.current.scrollHeight - modulesTable.current.clientHeight;

      if (!distanceBottom) {
        setDistanceBottom(Math.round(bottom * 0.2));
      }

      if (
        // @ts-ignore
        modulesTable.current.scrollTop > bottom - distanceBottom &&
        !loading &&
        lastEvaluatedItemKey !== ""
      ) {
        loadMore();
      }
    },
    // eslint-disable-next-line
    [loadMore, loading, distanceBottom]
  );

  useLayoutEffect(() => {
    const tableRef = modulesTable.current;
    // @ts-ignore
    tableRef.addEventListener("scroll", scrollListener);
    return () => {
      // @ts-ignore
      tableRef.removeEventListener("scroll", scrollListener);
    };
  }, [scrollListener]);

  useEffect(
    () => {
      setLoading(true);
      fetchModules(`search?limit=${fetchDataLimit}&q=${searchString}`).then(
        (data) => {
          setLastEvaluatedItemKey(
            data.lastEvaluatedItem ? data.lastEvaluatedItem.id : ""
          );
          setModules(data.modules);
          setLoading(false);
        }
      );
    },
    // eslint-disable-next-line
    [debouncedSearchTerm]
  );

  const fetchModules = async (api: string) => {
    const response = await fetch(api);
    return await response.json();
  };

  const handleSearchInputChange = (event: {
    target: { value: React.SetStateAction<string> };
  }) => {
    setSearchString(event.target.value);
  };

  const renderLastItem = () => {
    if (!modules || modules.length === 0) {
      return null;
    }
    return hasMoreData(lastEvaluatedItemKey) ? null : (
      <Paper>
        <Typography textAlign={"center"}>No more modules found</Typography>
      </Paper>
    );
  };

  return (
    <>
      <TextField
        id="module-search"
        label="Search module"
        type="search"
        value={searchString}
        onChange={handleSearchInputChange}
      />
      <List
        component={Stack}
        sx={{ maxHeight: "50vh", overflow: "auto", display: "flex-grow" }}
        // @ts-ignore
        ref={modulesTable}
      >
        {modules && modules.length > 0 ? (
          modules.map((module) => (
            <ModuleElement
              key={`${module.namespace}${module.name}${module.provider}`}
              module={module}
            />
          ))
        ) : !loading ? (
          <Box sx={{ margin: "auto" }}>
            <img alt={"Tapir logo"} src={tapirLogo} />
            <Typography textAlign={"center"}>
              Found this Tapir, but no modules
            </Typography>
          </Box>
        ) : null}
        {renderLastItem()}
        {loading ? (
          <CircularProgress color={"secondary"} sx={{ margin: "auto" }} />
        ) : null}
      </List>
    </>
  );
};

export default Overview;
