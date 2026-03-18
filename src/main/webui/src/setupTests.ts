import "@testing-library/jest-dom/vitest";
import { toHaveNoViolations } from "jest-axe";
import { server } from "./test/mocks/server";

expect.extend(toHaveNoViolations);

beforeAll(() => server.listen({ onUnhandledRequest: "warn" }));
afterEach(() => server.resetHandlers());
afterAll(() => server.close());
