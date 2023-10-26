provider "aws" {
  region = "eu-west-1"
  default_tags {
    tags = {
      "Deployment" = "tapir-eks-example"
    }
  }
}

provider "kubernetes" {
    config_path = "~/.kube/config"
}

data "aws_caller_identity" "current" {}
data "aws_region" "current" {}
data "aws_availability_zones" "available" {}

variable "auth_endpoint" {
  type = string
  description = "The endpoint of the IDP to authenticate against"
}

variable "auth_client_id" {
  type = string
    description = "The client ID to use when authenticating against the IDP"
}


variable "hosted_zone_name" {
    type = string
    description = "The name of the root hosted zone to create records in"
}

variable "eks_oidc_issuer_url" {
    type = string
    description = "The URL on the EKS cluster OIDC Issuer"
}

locals {
  name            = "tapir"
  hosted_zone_name  = var.hosted_zone_name
  eks_oidc_issuer_url = var.eks_oidc_issuer_url
}
