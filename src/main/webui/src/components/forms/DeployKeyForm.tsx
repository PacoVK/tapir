import React, { useState } from "react";
import {
  TextField,
  Button,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  SelectChangeEvent,
} from "@mui/material";
import { SnackBarSeverity } from "../notification/SnackBar";

type DeployKeyFormProps = {
  notifyUser: (severity: SnackBarSeverity, message: string) => void;
};

const DeployKeyForm = (props: DeployKeyFormProps) => {
  const [name, setName] = useState("");
  const [srcUrl, setSrcUrl] = useState("");
  const [moduleProvider, setModuleProvider] = useState("");
  const [namespace, setNamespace] = useState("");
  const [srcType, setSrcType] = React.useState("module");

  const { notifyUser } = props;

  const handleSrcTypeChange = (event: SelectChangeEvent) => {
    setSrcType(event.target.value as string);
  };

  const createResource = async (event: any) => {
    event.preventDefault();
    const id =
      `${namespace}-${name}` +
      (srcType === "module" ? `-${moduleProvider}` : "");
    const response = await fetch(`management/deploykey/${id}`, {
      method: "POST",
    });
    if (response.status === 200) {
      setSrcUrl("");
      setName("");
      setModuleProvider("");
      setNamespace("");
      notifyUser("success", `DeployKey ${id} for ${srcType} created`);
    } else {
      notifyUser(
        "error",
        `DeployKey for ${srcType} ${id} could not be created`,
      );
    }
    return await response.json();
  };

  return (
    <>
      <form onSubmit={createResource}>
        <FormControl fullWidth sx={{ mb: 4 }}>
          <InputLabel id="tapir-source-type-select-label">Type</InputLabel>
          <Select
            labelId="tapir-source-type-select-label"
            id="tapir-source-type-select"
            value={srcType}
            label="Type"
            onChange={handleSrcTypeChange}
          >
            <MenuItem value={"module"}>Module</MenuItem>
            <MenuItem value={"provider"}>Provider</MenuItem>
          </Select>
        </FormControl>
        <TextField
          type="url"
          variant="outlined"
          color="secondary"
          label="Source Repository URL"
          onChange={(e) => setSrcUrl(e.target.value)}
          value={srcUrl}
          fullWidth
          required
          sx={{ mb: 4 }}
        />
        <TextField
          type="text"
          variant="outlined"
          color="secondary"
          label="Namespace"
          onChange={(e) => setNamespace(e.target.value)}
          value={namespace}
          required
          fullWidth
          sx={{ mb: 4 }}
        />
        {srcType === "module" ? (
          <>
            <TextField
              type="text"
              variant="outlined"
              color="secondary"
              label="Name"
              onChange={(e) => setName(e.target.value)}
              value={name}
              required
              fullWidth
              sx={{ mb: 4 }}
            />
            <TextField
              type="text"
              variant="outlined"
              color="secondary"
              label="Provider"
              onChange={(e) => setModuleProvider(e.target.value)}
              value={moduleProvider}
              required
              fullWidth
              sx={{ mb: 4 }}
            />
          </>
        ) : (
          <>
            <TextField
              type="text"
              variant="outlined"
              color="secondary"
              label="Type"
              onChange={(e) => setName(e.target.value)}
              value={name}
              required
              fullWidth
              sx={{ mb: 4 }}
            />
          </>
        )}
        <Button variant="outlined" color="secondary" type={"submit"}>
          Create
        </Button>
      </form>
    </>
  );
};

export default DeployKeyForm;
