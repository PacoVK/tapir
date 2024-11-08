import React, {
  useCallback,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from "react";
import {
  CircularProgress,
  Grid2 as Grid,
  List,
  Stack,
  TextField,
} from "@mui/material";
import { Provider } from "../types";
import useDebounce from "../hooks/useDebounce";
import ProviderElement from "../components/list/ProviderElement";
import NotFoundInfo from "../components/layout/NotFoundInfo";

const fetchDataLimit = 5;
const ProviderOverview = () => {
  const providersTable = useRef();
  const [providers, setProviders] = useState([] as Provider[]);
  const [lastEvaluatedItemKey, setLastEvaluatedItemKey] = useState("");
  const [searchString, setSearchString] = useState("");
  const debouncedSearchTerm = useDebounce(searchString, 500);

  const [loading, setLoading] = useState(false);
  const [distanceBottom, setDistanceBottom] = useState(0);

  const loadMore = useCallback(
    () => {
      setLoading(true);
      fetchProviders(
        `search/providers?limit=${fetchDataLimit}&lastKey=${lastEvaluatedItemKey}&q=${searchString}`,
      ).then((data) => {
        const allProviders = [...providers, ...data.entities];
        setLastEvaluatedItemKey(
          data.lastEvaluatedItemId ? data.lastEvaluatedItemId : "",
        );
        setProviders(allProviders);
        setLoading(false);
      });
    },
    // eslint-disable-next-line
    [providers],
  );

  const scrollListener = useCallback(
    () => {
      let bottom =
        // @ts-ignore
        providersTable.current.scrollHeight -
        // @ts-ignore
        providersTable.current.clientHeight;

      if (!distanceBottom) {
        setDistanceBottom(Math.round(bottom * 0.2));
      }

      if (
        // @ts-ignore
        providersTable.current.scrollTop > bottom - distanceBottom &&
        !loading &&
        lastEvaluatedItemKey !== ""
      ) {
        loadMore();
      }
    },
    // eslint-disable-next-line
    [loadMore, loading, distanceBottom],
  );

  useLayoutEffect(() => {
    const tableRef = providersTable.current;
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
      fetchProviders(
        `search/providers?limit=${fetchDataLimit}&q=${searchString}`,
      ).then((data) => {
        setLastEvaluatedItemKey(
          data.lastEvaluatedItemId ? data.lastEvaluatedItemId : "",
        );
        setProviders(data.entities);
        setLoading(false);
      });
    },
    // eslint-disable-next-line
    [debouncedSearchTerm],
  );

  const fetchProviders = async (api: string) => {
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
        id="provider-search"
        label="Search provider"
        type="search"
        value={searchString}
        onChange={handleSearchInputChange}
      />
      <List
        component={Stack}
        sx={{ maxHeight: "50vh", overflow: "auto", display: "flex-grow" }}
        // @ts-ignore
        ref={providersTable}
      >
        {providers && providers.length > 0 ? (
          <Grid container spacing={2}>
            {providers.map((provider) => (
              <ProviderElement
                key={`${provider.namespace}${provider.type}`}
                provider={provider}
              />
            ))}
          </Grid>
        ) : !loading ? (
          <NotFoundInfo entity={"providers"} />
        ) : null}
        {loading ? (
          <CircularProgress color={"secondary"} sx={{ margin: "auto" }} />
        ) : null}
      </List>
    </>
  );
};

export default ProviderOverview;
