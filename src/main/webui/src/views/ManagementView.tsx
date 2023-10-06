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
  Tab,
  Tabs,
  TextField,
  Typography,
} from "@mui/material";
import { DeployKey } from "../types";
import DeployKeyForm from "../components/forms/DeployKeyForm";
import TabPanel from "../components/tab/TabPanel";
import useDebounce from "../hooks/useDebounce";
import DeployKeyElement from "../components/list/DeployKeyElement";
import SnackBar, {
  SnackBarSeverity,
} from "../components/notification/SnackBar";
import NotFoundInfo from "../components/layout/NotFoundInfo";

const fetchDataLimit = 10;

const ManagementView = () => {
  const deployKeysTable = useRef();
  const [lastEvaluatedItemKey, setLastEvaluatedItemKey] = useState("");
  const [searchString, setSearchString] = useState("");
  const debouncedSearchTerm = useDebounce(searchString, 500);
  const [distanceBottom, setDistanceBottom] = useState(0);
  const [deployKeys, setDeployKeys] = useState([] as DeployKey[]);
  const [loading, setLoading] = useState(false);

  const hasMoreData = (lastEvaluatedItemId: string) => {
    return (
      !!lastEvaluatedItemId && deployKeys.at(0)?.id !== lastEvaluatedItemId
    );
  };

  const loadMore = useCallback(
    () => {
      setLoading(true);
      fetch(
        `search/deploykeys?limit=${fetchDataLimit}&lastKey=${encodeURIComponent(
          lastEvaluatedItemKey,
        )}&q=${searchString}`,
      ).then(async (response) => {
        const data = await response.json();
        const allDeployKeys = [...deployKeys, ...data.entities];
        setLastEvaluatedItemKey(
          data.lastEvaluatedItemId ? data.lastEvaluatedItemId : "",
        );
        setDeployKeys(allDeployKeys);
        setLoading(false);
      });
    },
    // eslint-disable-next-line
    [deployKeys],
  );

  const scrollListener = useCallback(
    () => {
      let bottom =
        // @ts-ignore
        deployKeysTable.current.scrollHeight -
          // @ts-ignore
          deployKeysTable.current.clientHeight;

      if (!distanceBottom) {
        setDistanceBottom(Math.round(bottom * 0.2));
      }

      if (
        // @ts-ignore
        deployKeysTable.current.scrollTop > bottom - distanceBottom &&
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
    if (deployKeysTable && !loading) {
      const tableRef = deployKeysTable.current;
      // @ts-ignore
      tableRef.addEventListener("scroll", scrollListener);
      return () => {
        // @ts-ignore
        tableRef.removeEventListener("scroll", scrollListener);
      };
    }
  }, [scrollListener]);

  const fetchDeployKeyss = async (api: string) => {
    const response = await fetch(api);
    return await response.json();
  };

  const handleSearchInputChange = (event: {
    target: { value: React.SetStateAction<string> };
  }) => {
    setSearchString(event.target.value);
  };

  const renderLastItem = () => {
    if (!deployKeys || deployKeys.length === 0) {
      return null;
    }
    return hasMoreData(lastEvaluatedItemKey) ? null : (
      <Paper>
        <Typography textAlign={"center"}>No more deployKeys found</Typography>
      </Paper>
    );
  };

  const [tab, setTab] = React.useState(0);
  const [openSnackBar, setOpenSnackBar] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");
  const [snackBarSeverity, setSnackBarSeverity] = useState(
    "info" as SnackBarSeverity,
  );

  const fetchDeployKeys = async () => {
    await fetch(
      `search/deploykeys?limit=${fetchDataLimit}&q=${searchString}`,
    ).then((data) => {
      const { status } = data;
      if (status === 200) {
        data.json().then((data) => {
          setLastEvaluatedItemKey(
            data.lastEvaluatedItemId ? data.lastEvaluatedItemId : "",
          );
          setDeployKeys(data.entities);
        });
        setLoading(false);
      }
    });
  };

  useEffect(
    () => {
      setLoading(true);
      fetchDeployKeys();
    },
    // eslint-disable-next-line
    [debouncedSearchTerm],
  );

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    if (newValue === 0) {
      setLoading(true);
      fetchDeployKeys();
    }
    setTab(newValue);
  };

  const deleteDeployKey = async (deployKeyId: string) => {
    const response = await fetch(`management/deploykey/${deployKeyId}`, {
      method: "DELETE",
    });
    if (response.status === 200) {
      openSnackBarWithMessage("success", `DeployKey ${deployKeyId} deleted`);
    } else {
      openSnackBarWithMessage(
        "error",
        `DeployKey ${deployKeyId} could not be deleted`,
      );
    }
    deployKeys.splice(
      deployKeys.findIndex((deployKey) => deployKey.id === deployKeyId),
      1,
    );
    setDeployKeys([...deployKeys]);
  };

  const openSnackBarWithMessage = (
    severity: SnackBarSeverity,
    message: string,
  ) => {
    setSnackBarSeverity(severity);
    setSnackBarMessage(message);
    setOpenSnackBar(true);
  };

  const handleCloseSnackBar = () => {
    setOpenSnackBar(false);
  };

  const copyToClipBoard = (deployKeyId: string) => {
    openSnackBarWithMessage(
      "success",
      `DeployKey ${deployKeyId} copied to clipboard`,
    );
  };

  const regenerateDeployKey = async (deployKeyId: string) => {
    const response = await fetch(`management/deploykey/${deployKeyId}`, {
      method: "PUT",
    });
    if (response.status === 200) {
      openSnackBarWithMessage(
        "success",
        `DeployKey ${deployKeyId} regenerated`,
      );
    } else {
      openSnackBarWithMessage(
        "error",
        `DeployKey ${deployKeyId} could not be regenerated`,
      );
    }
    const modifiedKey = await response.json();
    const modifiedKeys = deployKeys.map((deployKey) => {
      if (deployKey.id === deployKeyId) {
        return modifiedKey;
      }
      return deployKey;
    });
    setDeployKeys([...modifiedKeys]);
  };

  return (
    <>
      <div>
        <Typography variant={"h3"}>Tapir Management</Typography>
      </div>
      {loading ? (
        <CircularProgress color={"secondary"} sx={{ margin: "auto" }} />
      ) : (
        <>
          <SnackBar
            severity={snackBarSeverity}
            message={snackBarMessage}
            open={openSnackBar}
            handleClose={handleCloseSnackBar}
          />
          <Box sx={{ width: "100%" }}>
            <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
              <Tabs
                value={tab}
                onChange={handleTabChange}
                aria-label="management tabs"
              >
                <Tab label="DeployKeys" />
                <Tab label="Create new DeployKey" />
              </Tabs>
            </Box>
            <TabPanel value={tab} index={0}>
              <TextField
                fullWidth
                id="deploykey-search"
                label="Search deploykey"
                type="search"
                value={searchString}
                onChange={handleSearchInputChange}
              />
              <List
                component={Stack}
                sx={{
                  maxHeight: "50vh",
                  overflow: "auto",
                  display: "flex-grow",
                }}
                // @ts-ignore
                ref={deployKeysTable}
              >
                {deployKeys && deployKeys.length > 0
                  ? deployKeys.map((deployKey) => (
                      <DeployKeyElement
                        key={deployKey.id}
                        deployKey={deployKey}
                        onCopy={copyToClipBoard}
                        onDelete={deleteDeployKey}
                        onRegenerate={regenerateDeployKey}
                      />
                    ))
                  : <NotFoundInfo entity={"keys"} />}
                {renderLastItem()}
              </List>
            </TabPanel>
            <TabPanel value={tab} index={1}>
              <DeployKeyForm notifyUser={openSnackBarWithMessage} />
            </TabPanel>
          </Box>
        </>
      )}
    </>
  );
};

export default ManagementView;
