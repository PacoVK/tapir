provider "aws" {
  region = "eu-west-1"
}

data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

resource "aws_apprunner_service" "tapir" {
  service_name = "tapir"

  source_configuration {
    image_repository {
      image_configuration {
        port = "8080"
        runtime_environment_variables = {
          S3_STORAGE_BUCKET_NAME   = aws_s3_bucket.storage.bucket
          S3_STORAGE_BUCKET_REGION = aws_s3_bucket.storage.region
          STORAGE_ACCESS_SESSION_DURATION = 60
        }
      }
      image_identifier      = "public.ecr.aws/pacovk/tapir:latest"
      image_repository_type = "ECR_PUBLIC"
    }
    auto_deployments_enabled = false
  }

  instance_configuration {
    instance_role_arn = aws_iam_role.tapir.arn
  }

  tags = {
    Name = "tapir"
  }
}

resource "aws_iam_role" "tapir" {
  name = "Tapir"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "tasks.apprunner.amazonaws.com"
        }
      },
    ]
  })
}

resource "aws_iam_role_policy" "tapir" {
  name   = "TapirPermissions"
  policy = data.aws_iam_policy_document.tapir.json
  role   = aws_iam_role.tapir.id
}

resource "aws_s3_bucket" "storage" {
  bucket        = "pacovk-tapir-storage"
  force_destroy = true
}

data "aws_iam_policy_document" "tapir" {
  statement {
    effect = "Allow"
    actions = [
      "dynamodb:PutItem",
      "dynamodb:Scan",
      "dynamodb:Query",
      "dynamodb:UpdateItem",
      "dynamodb:CreateTable",
      "dynamodb:DescribeTable",
      "dynamodb:GetItem"
    ]
    resources = [
      "arn:aws:dynamodb:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:table/Modules",
      "arn:aws:dynamodb:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:table/SecurityReports"
    ]
  }
  statement {
    effect = "Allow"
    actions = [
      "s3:*"
    ]
    resources = [aws_s3_bucket.storage.arn, "${aws_s3_bucket.storage.arn}/*"]
  }
}

output "url" {
  value = aws_apprunner_service.tapir.service_url
}