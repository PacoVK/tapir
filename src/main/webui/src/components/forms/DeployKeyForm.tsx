import React, { useState } from "react";
import {
  TextField,
  Button,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  SelectChangeEvent, capitalize,
} from "@mui/material";
import { SnackBarSeverity } from "../notification/SnackBar";

type DeployKeyFormProps = {
  notifyUser: (severity: SnackBarSeverity, message: string) => void;
};

const ModuleScopes = ["namespace", "name", "provider"];
const ProviderScopes = ["namespace", "type"];

const DeployKeyForm = (props: DeployKeyFormProps) => {
  const [name, setName] = useState("");
  const [srcUrl, setSrcUrl] = useState("");
  const [moduleProvider, setModuleProvider] = useState("");
  const [namespace, setNamespace] = useState("");
  const [srcType, setSrcType] = React.useState("module");
  const [scope, setScope] = useState("provider");

  const { notifyUser } = props;

  const handleSrcTypeChange = (event: SelectChangeEvent) => {
    const value = event.target.value as string;
    setSrcType(value);
    if (value === "module") {
      setScope("provider");
    } else {
      setScope("type");
    }
  };

  const handleScopeChange = (event: SelectChangeEvent) => {
    setScope(event.target.value as string);
  }

  const createResource = async (event: any) => {
    event.preventDefault();
    const body = new FormData();
    body.append("resourceType", srcType);
    body.append("scope", scope);
    body.append("namespace", namespace);
    body.append("source", srcUrl);
    if (srcType === "module") {
      if (scope === "name" || scope === "provider") {
        body.append("name", name);
      }
      if (scope === "provider") {
        body.append("provider", moduleProvider);
      }
    } else if (srcType === "provider") {
      if (scope == "type") {
        body.append("type", name);
      }
    }
    const response = await fetch(`management/deploykey`, {
      method: "POST",
      body,
    });
    if (response.status === 200) {
      setSrcUrl("");
      setName("");
      setModuleProvider("");
      setNamespace("");
      setScope("namespace");
      notifyUser("success", `DeployKey for ${srcType} created`);
    } else {
      notifyUser(
        "error",
        `DeployKey for ${srcType} could not be created`,
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
            required
            sx={{ mb: 4 }}
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
        <FormControl fullWidth sx={{ mb: 4 }}>
        <InputLabel id="tapir-scope-select-label">Scope</InputLabel>
        <Select
            labelId="tapir-scope-select-label"
            id="tapir-scope-select"
            value={scope}
            label="Scope"
            onChange={handleScopeChange}
        >
          {
            (srcType === "module" ? ModuleScopes : ProviderScopes).map((s) => <MenuItem key={s} value={s}>{capitalize(s)}</MenuItem>)
          }
        </Select>
        </FormControl>
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
            {
              scope === "name" || scope == "provider" ? (
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
              ): null
            }
            {
              scope == "provider" ? (
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
              ): null
            }
          </>
        ) : (
          <>

          {
            scope == "type" ? (
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
                ): null
          }
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
