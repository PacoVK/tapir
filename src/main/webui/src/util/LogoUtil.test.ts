import { getProviderLogo } from "./LogoUtil";
import { ProviderType } from "../types";
import awsLogo from "../assets/aws-icon.png";
import gcpLogo from "../assets/google-icon.png";
import azureLogo from "../assets/azurerm-icon.png";
import k8Logo from "../assets/kubernetes-icon.png";
import defaultLogo from "../assets/terraform-icon.png";

describe("getProviderLogo", () => {
  it("returns AWS logo for ProviderType.AWS", () => {
    const result = getProviderLogo(ProviderType.AWS);
    expect(result).toBeDefined();
    expect(result).toBe(awsLogo);
  });

  it("returns Google logo for ProviderType.GOOGLE", () => {
    const result = getProviderLogo(ProviderType.GOOGLE);
    expect(result).toBeDefined();
    expect(result).toBe(gcpLogo);
  });

  it("returns Azure logo for ProviderType.AZURE", () => {
    const result = getProviderLogo(ProviderType.AZURE);
    expect(result).toBeDefined();
    expect(result).toBe(azureLogo);
  });

  it("returns Kubernetes logo for ProviderType.KUBERNETES", () => {
    const result = getProviderLogo(ProviderType.KUBERNETES);
    expect(result).toBeDefined();
    expect(result).toBe(k8Logo);
  });

  it("returns default Terraform logo for unknown provider", () => {
    const result = getProviderLogo("unknown" as ProviderType);
    expect(result).toBeDefined();
    expect(result).toBe(defaultLogo);
  });
});
