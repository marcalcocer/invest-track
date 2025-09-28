// @ts-check
import { defineConfig } from "astro/config";
import tailwind from "@astrojs/tailwind";
import react from "@astrojs/react";

// https://astro.build/config
export default defineConfig({
  integrations: [tailwind(), react()],
  base: "/",
  server: {
    host: true, // Use 0.0.0.0
    port: 4321,
  },
});
