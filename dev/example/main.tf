module "testme" {
  source = "127.0.0.1:8443/paco/testme/aws"
  version = "1.4.0"
}

terraform {
  required_providers {
    foo = {
      source = "127.0.0.1:8443/paco/testme"
    }
  }
}