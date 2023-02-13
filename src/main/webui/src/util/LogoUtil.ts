import { ProviderType } from "../types";
import awsLogo from "../assets/aws-icon.png";
import gcpLogo from "../assets/google-icon.png";
import azureLogo from "../assets/azurerm-icon.png";
import k8Logo from "../assets/kubernetes-icon.png";
import defaultLogo from "../assets/terraform-icon.png";

export const getProviderLogo = (provider: ProviderType) => {
  switch (provider) {
    case ProviderType.AWS:
      return awsLogo;
    case ProviderType.GOOGLE:
      return gcpLogo;
    case ProviderType.AZURE:
      return azureLogo;
    case ProviderType.KUBERNETES:
      return k8Logo;
    default:
      return defaultLogo;
  }
};
