import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      "/tapir": "http://localhost:8080",
      "/search": "http://localhost:8080",
      "/terraform": "http://localhost:8080",
      "/reports": "http://localhost:8080",
      "/management": "http://localhost:8080",
      "/logout": "http://localhost:8080",
      "/.well-known": "http://localhost:8080",
      "/q": "http://localhost:8080",
    },
  },
  test: {
    globals: true,
    environment: "jsdom",
    setupFiles: "./src/setupTests.ts",
    css: true,
  },
});
