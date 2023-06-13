import React, {
  useCallback,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from "react";
import {
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
import NotFoundInfo from "../components/layout/NotFoundInfo";
import {fetchApi} from "../services/ApiService";

const fetchDataLimit = 5;
const ModuleOverview = () => {
  const modulesTable = useRef();
  const [modules, setModules] = useState([] as Module[]);
  const [lastEvaluatedItemKey, setLastEvaluatedItemKey] = useState("");
  const [searchString, setSearchString] = useState("");
  const debouncedSearchTerm = useDebounce(searchString, 500);

  const [loading, setLoading] = useState(false);
  const [distanceBottom, setDistanceBottom] = useState(0);

  const hasMoreData = (lastEvaluatedItemId: string) => {
    return !!lastEvaluatedItemId && modules.at(0)?.id !== lastEvaluatedItemId;
  };

  const loadMore = useCallback(
    () => {
      setLoading(true);
      fetchModules(
        `search/modules?limit=${fetchDataLimit}&lastKey=${encodeURIComponent(
          lastEvaluatedItemKey
        )}&q=${searchString}`
      ).then((data) => {
        const allModules = [...modules, ...data.entities];
        setLastEvaluatedItemKey(
          data.lastEvaluatedItemId ? data.lastEvaluatedItemId : ""
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
      fetchModules(
        `search/modules?limit=${fetchDataLimit}&q=${searchString}`
      ).then((data) => {
        setLastEvaluatedItemKey(
          data.lastEvaluatedItemId ? data.lastEvaluatedItemId : ""
        );
        setModules(data.entities);
        setLoading(false);
      });
    },
    // eslint-disable-next-line
    [debouncedSearchTerm]
  );

  const fetchModules = async (path: string) => {
    return  await fetchApi(path);
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
          <NotFoundInfo entity={"modules"} />
        ) : null}
        {renderLastItem()}
        {loading ? (
          <CircularProgress color={"secondary"} sx={{ margin: "auto" }} />
        ) : null}
      </List>
    </>
  );
};

export default ModuleOverview;
