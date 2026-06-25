// @ts-check
import { defineConfig } from "astro/config";
import react from "@astrojs/react";

// https://astro.build/config
export default defineConfig({
  integrations: [react()],
  base: '/invest-track/',
  server: {
    host: true, // Use 0.0.0.0
    port: 4321,
    allowedHosts: ["apps.home"],
    hmr: {
      host: 'apps.home',
      clientPort: 80,
    },
  },
  vite: {
    server: {
      proxy: {
        '/ollama/api/': 'http://localhost:5000',
      },
    },
  },
});
