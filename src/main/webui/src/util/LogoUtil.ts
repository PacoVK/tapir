import { Provider } from "../types";
import awsLogo from "../assets/aws-icon.png";
import gcpLogo from "../assets/google-icon.png";
import azureLogo from "../assets/azurerm-icon.png";
import k8Logo from "../assets/kubernetes-icon.png";
import defaultLogo from "../assets/terraform-icon.png";

export const getProviderLogo = (provider: Provider) => {
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
};
