import {useLocation} from "react-router-dom";
import tapirLogo from "../assets/tapir.png";
import {Box, Typography} from "@mui/material";
import React from "react";

const NotFoundPage = () => {
    return (
        <Box sx={{ margin: "auto" }}>

            <Typography variant={"h2"} textAlign={"center"}>
                404 - Page not found
            </Typography>
            <center><img alt={"Tapir logo"} src={tapirLogo} /></center>
        </Box>
    )
}

export default NotFoundPage