import React, {
  useCallback,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from "react";
import { List, Stack, TextField } from "@mui/material";
import ModuleElement from "../components/list/ModuleElement";
import { Module } from "../types";
import useDebounce from "../hooks/useDebounce";

const fetchDataLimit = 5;
const Overview = () => {
  const modulesTable = useRef();
  const [modules, setModules] = useState([] as Module[]);
  const [lastEvaluatedItem, setLastEvaluatedItem] = useState("");
  const [searchString, setSearchString] = useState("");
  const debouncedSearchTerm = useDebounce(searchString, 500);

  const [loading, setLoading] = useState(false);
  const [distanceBottom, setDistanceBottom] = useState(0);

  const loadMore = useCallback(() => {
    setLoading(true);
    fetchModules(
      `search?limit=${fetchDataLimit}&lastKey=${lastEvaluatedItem}&q=${searchString}`
    ).then((data) => {
      // TODO handle no more modules available
      const allModules = [...modules, ...data.modules];
      setLastEvaluatedItem(
        data.lastEvaluatedItem ? data.lastEvaluatedItem : ""
      );
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
    fetchModules(`search?limit=${fetchDataLimit}&q=${searchString}`).then(
      (data) => {
        setLastEvaluatedItem(
          data.lastEvaluatedItem ? data.lastEvaluatedItem : ""
        );
        setModules(data.modules);
        setLoading(false);
      }
    );
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
    </>
  );
};

export default Overview;
