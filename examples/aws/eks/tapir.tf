resource "kubernetes_namespace_v1" "tapir" {
    metadata {
        name = local.name
    }
}

resource "kubernetes_deployment_v1" "tapir" {
  metadata {
    namespace = kubernetes_namespace_v1.tapir.id
    name      = local.name
    labels = merge(
      { name = local.name }
    )
    annotations = {
      "eks.amazonaws.com/role-arn" = aws_iam_role.tapir.arn
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        name = local.name
      }
    }

    template {
      metadata {
        labels = {
          name = local.name
        }
        annotations = {
          "eks.amazonaws.com/role-arn" = aws_iam_role.tapir.arn
        }
      }

      spec {
        service_account_name = local.name
        container {
          image = "pacovk/tapir"
          name  = local.name

          port {
            container_port = 8080
          }

          env {
            name  = "S3_STORAGE_BUCKET_NAME"
            value = aws_s3_bucket.tapir.id
          }
          env {
            name  = "S3_STORAGE_BUCKET_REGION"
            value = data.aws_region.current.name
          }
          env {
            name  = "REGISTRY_HOSTNAME"
            value = "tapir.${data.aws_route53_zone.example.name}"
          }
          env {
            name  = "REGISTRY_PORT"
            value = 443
          }
          env {
            name  = "AUTH_ENDPOINT"
            value = var.auth_endpoint
          }
          env {
            name  = "AUTH_CLIENT_ID"
            value = var.auth_client_id
          }
        }
      }
    }
  }
}

resource "kubernetes_service_v1" "tapir" {
  metadata {
    name      = local.name
    namespace = kubernetes_namespace_v1.tapir.id
    annotations = {
      "external-dns.alpha.kubernetes.io/hostname" = "tapir.${data.aws_route53_zone.example.name}"
    }
  }
  spec {
    selector = {
      name = local.name
    }
    port {
      port        = 443
      target_port = 8080
    }

    type = "ClusterIP"
  }
}

resource "kubernetes_ingress_v1" "tapir" {
  metadata {
    name      = local.name
    namespace = kubernetes_namespace_v1.tapir.id
    labels = {
      service = local.name
    }
    # Values here:https://kubernetes-sigs.github.io/aws-load-balancer-controller/v2.2/guide/ingress/annotations/
    annotations = {
      "kubernetes.io/ingress.class"                        = "alb"
      "alb.ingress.kubernetes.io/group.name"               = "default"
      "alb.ingress.kubernetes.io/scheme"                   = "internet-facing"
      "alb.ingress.kubernetes.io/backend-protocol"         = "HTTPS"
      "alb.ingress.kubernetes.io/target-type"              = "ip"
      "alb.ingress.kubernetes.io/listen-ports"             = "[{\"HTTPS\": 443}]"
      "alb.ingress.kubernetes.io/ssl-policy"               = "ELBSecurityPolicy-TLS-1-2-Ext-2018-06"
      "alb.ingress.kubernetes.io/certificate-arn"          = aws_acm_certificate.example.arn
    }
  }
  spec {
    rule {
      host = "tapir.${data.aws_route53_zone.example.name}"
      http {
        path {
          path      = "/*"
          path_type = "ImplementationSpecific"
          backend {
            service {
              name = kubernetes_service_v1.tapir.metadata[0].name
              port {
                number = 443
              }
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_service_account_v1" "tapir" {
  metadata {
    name      = local.name
    namespace = kubernetes_namespace_v1.tapir.id
    annotations = {
      "eks.amazonaws.com/role-arn" = aws_iam_role.tapir.arn
    }
  }
  automount_service_account_token = false
}

resource "kubernetes_cluster_role" "tapir" {
  metadata {
    name = local.name
  }

  rule {
    api_groups = [""]
    resources  = ["services", "endpoints", "pods"]
    verbs      = ["get", "watch", "list"]
  }

  rule {
    api_groups = ["extensions", "networking.k8s.io"]
    resources  = ["ingresses"]
    verbs      = ["get", "watch", "list"]
  }

  rule {
    api_groups = [""]
    resources  = ["nodes"]
    verbs      = ["list", "watch"]
  }
}

resource "kubernetes_cluster_role_binding" "tapir" {
  metadata {
    name = local.name
  }

  role_ref {
    api_group = "rbac.authorization.k8s.io"
    kind      = "ClusterRole"
    name      = kubernetes_cluster_role.tapir.metadata[0].name
  }

  subject {
    kind      = "ServiceAccount"
    name      = local.name
    namespace = kubernetes_namespace_v1.tapir.id
  }
}

resource "aws_dynamodb_table" "modules" {
    name                        = "Modules"
    billing_mode                = "PAY_PER_REQUEST" 
    hash_key                    = "id"
    region                      = data.aws_region.current.name
    attribute {
        name = "id"
        type = "S"
    }
}

resource "aws_dynamodb_table" "providers" {
    name                        = "Providers"
    billing_mode                = "PAY_PER_REQUEST" 
    hash_key                    = "id"
    region                      = data.aws_region.current.name
    attribute {
        name = "id"
        type = "S"
    }
}

resource "aws_dynamodb_table" "reports" {
    name                        = "Reports"
    billing_mode                = "PAY_PER_REQUEST" 
    hash_key                    = "id"
    region                      = data.aws_region.current.name
    attribute {
        name = "id"
        type = "S"
    }
}

resource "aws_dynamodb_table" "deploykeys" {
    name                        = "DeployKeys"
    billing_mode                = "PAY_PER_REQUEST" 
    hash_key                    = "id"
    region                      = data.aws_region.current.name
    attribute {
        name = "id"
        type = "S"
    }
}

resource "aws_iam_role" "tapir" {
  name               = local.name
  description        = "Role assumed by EKS ServiceAccount tapir"
  assume_role_policy = data.aws_iam_policy_document.tapir_sa.json
  tags = {
    "Resource" = "aws_iam_role.tapir"
  }
}

data "aws_iam_policy_document" "tapir_sa" {
  statement {
    effect  = "Allow"
    actions = ["sts:AssumeRoleWithWebIdentity"]
    principals {
      type = "Federated"
      identifiers = [
        "arn:aws:iam::${data.aws_caller_identity.current.account_id}:oidc-provider/${replace(local.eks_oidc_issuer_url, "https://", "")}"
      ]
    }
    condition {
      test     = "StringEquals"
      variable = "${replace(local.eks_oidc_issuer_url, "https://", "")}:sub"
      values   = ["system:serviceaccount:${kubernetes_namespace_v1.tapir.id}:${local.name}"]
    }
  }
}

resource "aws_iam_role_policy" "tapir" {
  name = "tapir"
  role   = aws_iam_role.tapir.id
  policy = data.aws_iam_policy_document.tapir.json
}

data "aws_iam_policy_document" "tapir" {
  statement {
    sid    = "S3Access"
    effect = "Allow"
    resources = [
      "${aws_s3_bucket.tapir.arn}/*",
      aws_s3_bucket.tapir.arn
    ]
    actions = [
      "s3:Describe*",
      "s3:List*",
      "s3:Get*",
      "s3:Put*"
    ]
  }

  statement {
    sid    = "DynamoDbAccess"
    effect = "Allow"
    resources = [
      "arn:aws:dynamodb:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:table/Modules",
      "arn:aws:dynamodb:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:table/Providers",
      "arn:aws:dynamodb:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:table/Reports",
      "arn:aws:dynamodb:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:table/DeployKeys"
    ]
    actions = [
      "dynamodb:PutItem",
      "dynamodb:Scan",
      "dynamodb:Query",
      "dynamodb:UpdateItem",
      "dynamodb:CreateTable",
      "dynamodb:DescribeTable",
      "dynamodb:GetItem"
    ]
  }
}
