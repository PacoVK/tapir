import { SyntheticEvent } from "react";
import { Alert, Snackbar, SnackbarCloseReason } from "@mui/material";

export type SnackBarSeverity = "success" | "info" | "warning" | "error";

type SnackBarProps = {
  severity: SnackBarSeverity;
  message: string;
  open: boolean;
  handleClose: (
    event: Event | SyntheticEvent<any, Event>,
    reason?: SnackbarCloseReason,
  ) => void;
};
const SnackBar = (props: SnackBarProps) => {
  const { severity, message, open, handleClose } = props;

  return (
    <Snackbar open={open} autoHideDuration={6000} onClose={handleClose}>
      <Alert onClose={handleClose} severity={severity} sx={{ width: "100%" }}>
        {message}
      </Alert>
    </Snackbar>
  );
};

export default SnackBar;
