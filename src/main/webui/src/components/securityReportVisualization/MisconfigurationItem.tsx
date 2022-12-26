import React from "react";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Link,
  Typography,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {Misconfiguration} from "../../types";
import "./MisconfigurationItem.css";

const MisconfigurationItem = ({
  misconfiguration,
  keyIdentifier,
}: {
  misconfiguration: Misconfiguration;
  keyIdentifier: string;
}) => {
  return (
    <Accordion key={misconfiguration.resource + keyIdentifier}>
      <AccordionSummary
        expandIcon={<ExpandMoreIcon />}
        aria-controls={`${misconfiguration.rule_description + keyIdentifier}-content`}
        id={`${misconfiguration.rule_description + keyIdentifier}-header`}
        className={`severity-${misconfiguration.severity}`}
      >
        <Typography>
          {misconfiguration.resource}, Line:{" "}
          {misconfiguration.location.start_line} -{" "}
          {misconfiguration.location.end_line}
        </Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Typography>
          {misconfiguration.severity} - {misconfiguration.impact}
        </Typography>
        <Typography>
          Resource: {misconfiguration.resource}
        </Typography>
        <Typography>
          StartLine: {misconfiguration.location.start_line}
          EndLine: {misconfiguration.location.end_line}
        </Typography>
        <Typography>Solution: {misconfiguration.resolution}</Typography>
        <Link href={misconfiguration.links[0]} rel="noopener" target="_blank">
          Read more
        </Link>
      </AccordionDetails>
    </Accordion>
  );
};

export default MisconfigurationItem;
