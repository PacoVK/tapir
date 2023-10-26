import { DeployKey } from "../../types";
import {
  Avatar,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  IconButton,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Tooltip,
  Typography,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import CopyIcon from "@mui/icons-material/ContentCopy";
import LoopIcon from "@mui/icons-material/Loop";
import KeyIcon from "@mui/icons-material/Key";
import React, { useState } from "react";
import { formatDateTime } from "../../util/DateUtil";

type Action = "delete" | "regenerate";

const DeployKeyElement = ({
  deployKey,
  onCopy,
  onRegenerate,
  onDelete,
}: {
  deployKey: DeployKey;
  onCopy: (deployKey: string) => void;
  onRegenerate: (deployKey: string) => void;
  onDelete: (deployKey: string) => void;
}) => {
  const [open, setOpen] = useState(false);
  const [action, setAction] = useState(undefined as Action | undefined);
  const [dialogTitle, setDialogTitle] = useState("");
  const [dialogMessage, setDialogMessage] = useState("");

  const handleDialogClose = () => {
    setAction(undefined);
    setOpen(false);
  };

  const openDeleteKeyDialog = () => {
    setDialogTitle(`Delete Deploykey ${deployKey.id}?`);
    setDialogMessage(
      `Are you sure you want to delete ${deployKey.id}? This is a destructive action and cannot be undone.`,
    );
    setAction("delete");
    setOpen(true);
  };

  const openRegenerateKeyDialog = () => {
    setDialogTitle(`Regenerate Deploykey for ${deployKey.id}?`);
    setDialogMessage(
      `Are you sure you want to regenerate ${deployKey.id}? You'll need to update the key in your CI/CD pipeline.`,
    );
    setAction("regenerate");
    setOpen(true);
  };

  const handleDestructiveAction = () => {
    switch (action) {
      case "regenerate":
        onRegenerate(deployKey.id);
        setOpen(false);
        break;
      case "delete":
        onDelete(deployKey.id);
        setOpen(false);
        break;
      default:
        setOpen(false);
        break;
    }
  };

  const copyToClipboard = async () => {
    await navigator.clipboard.writeText(deployKey.key);
    onCopy(deployKey.id);
  };

  return (
    <>
      <ListItem>
        <ListItemAvatar>
          <Avatar>
            <KeyIcon />
          </Avatar>
        </ListItemAvatar>
        <ListItemText
          primary={deployKey.id}
          secondary={
            <>
              <Typography component={"span"} variant={"subtitle1"}>
                {deployKey.key}
              </Typography>
              <br />
              <Typography component={"span"} variant={"subtitle2"}>
                Last modified at: {formatDateTime(deployKey.lastModifiedAt)}
              </Typography>
            </>
          }
        />
        <Tooltip title="Copy to clipboard">
          <IconButton
            edge="end"
            sx={{ mr: 1 }}
            aria-label="copy"
            onClick={copyToClipboard}
          >
            <CopyIcon />
          </IconButton>
        </Tooltip>
        <Tooltip title="Regenerate">
          <IconButton
            edge="end"
            sx={{ mr: 1 }}
            aria-label="regenerate"
            value={deployKey.id}
            onClick={openRegenerateKeyDialog}
          >
            <LoopIcon />
          </IconButton>
        </Tooltip>
        <Tooltip title="Delete">
          <IconButton
            edge="end"
            aria-label="delete"
            value={deployKey.id}
            onClick={openDeleteKeyDialog}
          >
            <DeleteIcon />
          </IconButton>
        </Tooltip>
      </ListItem>
      <Dialog
        open={open}
        onClose={handleDialogClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">
          <Typography>{dialogTitle}</Typography>
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            {dialogMessage}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDestructiveAction}>Confirm</Button>
          <Button onClick={handleDialogClose} autoFocus>
            Cancel
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default DeployKeyElement;
