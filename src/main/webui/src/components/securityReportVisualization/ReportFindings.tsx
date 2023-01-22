import React from "react";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Link,
  Typography,
} from "@mui/material";
import { Misconfiguration, ReportFindingsProps } from "../../types";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import MisconfigurationItem from "./MisconfigurationItem";

const ReportFindings = (props: ReportFindingsProps) => {
  const { reports } = props;
  return (
    <>
      <Typography variant="h4">Security Report</Typography>
      <Typography sx={{ mb: 2, mt: "1em" }}>
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
      {Object.entries(reports).map(([target, findings]: [string, any]) => {
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
      })}
    </>
  );
};

export default ReportFindings;
