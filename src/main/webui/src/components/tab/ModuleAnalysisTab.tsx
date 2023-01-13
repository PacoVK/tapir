import React from "react";
import { Tab, Tabs, Typography } from "@mui/material";
import { ModuleAnalysisTabProps } from "../../types";
import TabPanel from "./TabPanel";
import ModuleInput from "../documentation/ModuleInput";
import ModuleOutput from "../documentation/ModuleOutput";
import ModuleDependencies from "../documentation/ModuleDependencies";
import ReportFindings from "../securityReportVisualization/ReportFindings";
import ModuleResources from "../documentation/ModuleResources";

const ModuleAnalysisTab = (props: ModuleAnalysisTabProps) => {
  const { version, reports, documentation } = props;
  const [tab, setTab] = React.useState(0);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTab(newValue);
  };

  return documentation ? (
    <>
      <Tabs
        value={tab}
        onChange={handleTabChange}
        aria-label="module detail tabs"
      >
        <Tab label={`Inputs (${documentation.inputs.length})`} />
        <Tab label={`Outputs (${documentation.outputs.length})`} />
        <Tab
          label={`Dependencies (${
            documentation.modules.length + documentation.providers.length
          })`}
        />
        <Tab label={`Resources (${documentation.resources.length})`} />
        <Tab label="Security Report" />
      </Tabs>
      <TabPanel value={tab} index={0}>
        <ModuleInput inputs={documentation.inputs} />
      </TabPanel>
      <TabPanel value={tab} index={1}>
        <ModuleOutput outputs={documentation.outputs} />
      </TabPanel>
      <TabPanel value={tab} index={2}>
        <ModuleDependencies
          modules={documentation.modules}
          providers={documentation.providers}
        />
      </TabPanel>
      <TabPanel value={tab} index={3}>
        <ModuleResources resources={documentation.resources} />
      </TabPanel>
      <TabPanel value={tab} index={4}>
        {reports ? (
          <ReportFindings reports={reports} />
        ) : (
          <Typography>
            No report available for module version: {version}
          </Typography>
        )}
      </TabPanel>
    </>
  ) : null;
};

export default ModuleAnalysisTab;
