import React, { useEffect, useState } from "react";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Chip,
  FormControl,
  InputLabel,
  Link,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  Typography,
} from "@mui/material";
import { Misconfiguration } from "../types";
import { getProviderLogo } from "../util/LogoUtil";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import MisconfigurationItem from "../components/securityReportVisualization/MisconfigurationItem";
import DownloadIcon from "@mui/icons-material/Download";
import InfoIcon from "@mui/icons-material/Info";
import RocketLaunchIcon from "@mui/icons-material/RocketLaunch";
import { formatDate } from "../util/DateUtil";
import { useLocation, useParams } from "react-router-dom";

const ModuleDetails = () => {
  const routeParams = useParams();
  const location = useLocation();

  const [module, setModule] = useState(location.state?.data);
  const [reports, setReports] = useState();
  const [version, setVersion] = React.useState();

  useEffect(() => {
    if (module && version) {
      fetchModuleReports();
    }
  }, [version]);

  const fetchModule = async () => {
    const baseUrl = window.location.href.replace(location.pathname, "");
    const response = await fetch(
      `${baseUrl}/terraform/modules/v1/${routeParams.namespace}/${routeParams.name}/${routeParams.provider}`
    );
    const module = await response.json();
    setModule(module);
    setVersion(module.versions.at(0)?.version);
  };

  const loadingRoutine = () => {
    fetchModule();
    return <Typography>Loading...</Typography>;
  };

  const handleVersionChange = (event: SelectChangeEvent) => {
    // @ts-ignore
    setVersion(event.target.value);
  };
  const fetchModuleReports = async () => {
    const baseUrl = window.location.href.replace(location.pathname, "");
    const response = await fetch(
      `${baseUrl}/reports/${module.namespace}/${module.name}/${module.provider}/security/${version}`
    );
    const reports = await response.json();
    setReports(reports.securityReport);
  };

  return module && version ? (
    <>
      <Stack direction="row" spacing={2} sx={{ mb: 2 }}>
        <img
          style={{
            width: 64,
            height: 64,
          }}
          alt={"Provider logo"}
          src={getProviderLogo(module.provider)}
        />
        <Typography
          variant="h6"
          noWrap
          component="a"
          href="/"
          alignItems={"center"}
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
          {module.namespace}/{module.name}/{module.provider}
        </Typography>
      </Stack>
      <Stack direction="row" spacing={2} sx={{ mb: 5 }} alignItems={"center"}>
        <FormControl>
          <InputLabel id="module-version-select-label">Version</InputLabel>
          <Select
            labelId="module-versions-select-label"
            id="module-versions-select"
            value={version}
            label="Versions"
            onChange={handleVersionChange}
          >
            {module.versions.map((version: { version: string }) => {
              return (
                <MenuItem key={version.version} value={version.version}>
                  {version.version}
                </MenuItem>
              );
            })}
          </Select>
        </FormControl>
        <Chip
          icon={<DownloadIcon />}
          label={`Total downloads: ${module.downloads}
                        `}
        />
        <Chip
          icon={<InfoIcon />}
          label={`Latest version: ${module.versions.at(0)!.version}`}
        />
        <Chip
          icon={<RocketLaunchIcon />}
          label={`Last published at: ${formatDate(module.published_at)}`}
        />
      </Stack>
      <Typography sx={{ mb: 2 }}>
        Security findings by{" "}
        <Link
          href="https://aquasecurity.github.io/tfsec"
          rel="noopener"
          target="_blank"
        >
          Tfsec
        </Link>
        :
      </Typography>
      {reports ? (
        Object.entries(reports).map(([target, findings]: [string, any]) => {
          return (
            <Accordion key={target}>
              <AccordionSummary
                expandIcon={<ExpandMoreIcon />}
                aria-controls={`${target}-content`}
                id={`${target}-header`}
              >
                <Typography>
                  {target} - Findings: {findings.length}
                </Typography>
              </AccordionSummary>
              <AccordionDetails>
                {findings.map(
                  (misconfiguration: Misconfiguration, index: string) => {
                    return (
                      <MisconfigurationItem
                        key={`misconf-item-${index}`}
                        misconfiguration={misconfiguration}
                        keyIdentifier={index}
                      />
                    );
                  }
                )}
              </AccordionDetails>
            </Accordion>
          );
        })
      ) : (
        <Typography>
          No report available for module version: {version}
        </Typography>
      )}
    </>
  ) : (
    loadingRoutine()
  );
};

export default ModuleDetails;