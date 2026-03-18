import { http, HttpResponse } from "msw";
import {
  createDeployKey,
  createModule,
  createProviderWithVersionMap,
  createSearchResponse,
  createSecurityReport,
  createDocumentation,
  createUser,
} from "./fixtures";

export const handlers = [
  // Search endpoints
  http.get("*/search/modules", () => {
    return HttpResponse.json(createSearchResponse([createModule()]));
  }),

  http.get("*/search/providers", () => {
    return HttpResponse.json(
      createSearchResponse([createProviderWithVersionMap()]),
    );
  }),

  http.get("*/search/deploykeys", () => {
    return HttpResponse.json(createSearchResponse([createDeployKey()]));
  }),

  // Terraform registry protocol
  http.get("*/terraform/modules/v1/:namespace/:name/:provider", () => {
    return HttpResponse.json(createModule());
  }),

  http.get("*/terraform/providers/v1/:namespace/:type", () => {
    return HttpResponse.json(createProviderWithVersionMap());
  }),

  // Reports
  http.get("*/reports/:namespace/:name/:provider/security/:version", () => {
    return HttpResponse.json({
      securityReport: createSecurityReport(),
      documentation: createDocumentation(),
    });
  }),

  // Management
  http.post("*/management/deploykey", () => {
    return HttpResponse.json(createDeployKey({ id: "dk-new-key" }));
  }),

  http.delete("*/management/deploykey/:id", () => {
    return new HttpResponse(null, { status: 200 });
  }),

  http.put("*/management/deploykey/:id", ({ params }) => {
    return HttpResponse.json(
      createDeployKey({
        id: params.id as string,
        key: "regenerated-key-value",
      }),
    );
  }),

  // User / Auth
  http.get("*/tapir/user", () => {
    return HttpResponse.json(createUser());
  }),

  http.get("*/logout", () => {
    return new HttpResponse(null, { status: 200 });
  }),
];
