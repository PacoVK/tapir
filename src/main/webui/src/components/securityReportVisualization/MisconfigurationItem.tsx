import React from "react";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Link,
  Typography,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { Misconfiguration } from "../../types";
import "./MisconfigurationItem.css";

const MisconfigurationItem = ({
  misconfiguration,
  keyIdentifier,
}: {
  misconfiguration: Misconfiguration;
  keyIdentifier: string;
}) => {
  return (
    <Accordion key={misconfiguration.Title + keyIdentifier}>
      <AccordionSummary
        expandIcon={<ExpandMoreIcon />}
        aria-controls={`${misconfiguration.Title + keyIdentifier}-content`}
        id={`${misconfiguration.Title + keyIdentifier}-header`}
        className={`severity-${misconfiguration.Severity}`}
      >
        <Typography>
          {misconfiguration.CauseMetadata.Resource}, Line:{" "}
          {misconfiguration.CauseMetadata.StartLine} -{" "}
          {misconfiguration.CauseMetadata.EndLine}, {misconfiguration.Title}
        </Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Typography>
          {misconfiguration.Severity} - {misconfiguration.Message}
        </Typography>
        <Typography>
          Resource: {misconfiguration.CauseMetadata.Resource}
        </Typography>
        <Typography>
          StartLine: {misconfiguration.CauseMetadata.StartLine}
          EndLine: {misconfiguration.CauseMetadata.EndLine}
        </Typography>
        <Typography>Solution: {misconfiguration.Resolution}</Typography>
        <Link href={misconfiguration.PrimaryURL} rel="noopener" target="_blank">
          Read more
        </Link>
      </AccordionDetails>
    </Accordion>
  );
};

export default MisconfigurationItem;
