import React from "react";
import {Box, Chip, Paper, Stack} from "@mui/material";
import awsLogo from "./../../assets/aws-icon.png";
import gcpLogo from "./../../assets/google-icon.png";
import azureLogo from "./../../assets/azurerm-icon.png";
import k8Logo from "./../../assets/kubernetes-icon.png";
import defaultLogo from "./../../assets/terraform-icon.png";
import {Module, Provider} from "../../types";
import Typography from "@mui/material/Typography";
import DownloadIcon from '@mui/icons-material/Download';
import RocketLaunchIcon from '@mui/icons-material/RocketLaunch';
import InfoIcon from '@mui/icons-material/Info';



const getProviderLogo = (provider: Provider) => {
    switch (provider) {
        case Provider.AWS:
            return awsLogo;
        case Provider.GOOGLE:
            return gcpLogo;
        case Provider.AZURE:
            return azureLogo;
        case Provider.KUBERNETES:
            return k8Logo;
        default:
            return defaultLogo;
    }
}

const ModuleElement = ({module}: {module: Module}) => {
    return(
        <Box marginY={2}>
            <Paper>
                <Stack direction={"row"} spacing={4}>
                    <img
                        style={{
                            marginTop: 10,
                            marginBottom: 10,
                            marginLeft: 10,
                            width: 64,
                            height: 64,
                        }}
                        alt={"Provider logo"}
                        src={getProviderLogo(module.provider)}
                    />
                    <Box>
                        <Typography variant="h5" marginY={2}>
                            {module.namespace}/{module.name}
                        </Typography>
                    </Box>
                </Stack>
                <Box>
                    <Chip icon={<DownloadIcon />} label={`Total downloads: ${module.downloads}`} />
                    <Chip icon={<InfoIcon />} label={`Latest version: ${module.versions.at(module.versions.length -1)!.version}`} />
                    <Chip icon={<RocketLaunchIcon />} label={`Last published at: ${module.published_at}`} />
                </Box>
            </Paper>
        </Box>
    );
}

export default ModuleElement;