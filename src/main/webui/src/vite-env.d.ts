/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_VERSION: string;
  readonly VITE_TAPIR_DOCS: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
