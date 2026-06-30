import { defineConfig } from "astro/config";
import react from "@astrojs/react";
import { fileURLToPath } from "node:url";
function faviconPlugin() {
  return {
    name: "favicon",
    /** @param {import('vite').ViteDevServer} server */
    configureServer(server) {
      server.middlewares.use(
        /**
         * @param {import('vite').Connect.IncomingMessage} req
         * @param {import('http').ServerResponse} res
         * @param {import('vite').Connect.NextFunction} next
         */
        (req, res, next) => {
          if (req.url === "/favicon.ico") {
            res.writeHead(302, { Location: "/invest-track/favicon.svg" });
            res.end();
            return;
          }
          next();
        },
      );
    },
  };
}

// https://astro.build/config
export default defineConfig({
  integrations: [react()],
  base: '/invest-track/',
  server: {
    host: true, // Use 0.0.0.0
    port: 4321,
    allowedHosts: ["apps.home"],
  },
  vite: {
    plugins: [faviconPlugin()],
    resolve: {
      alias: {
        "@": fileURLToPath(new URL("./src", import.meta.url)),
      },
    },
    server: {
      ws: {
        host: 'apps.home',
        port: 4321,
      },
      proxy: {
        '/ollama/api/': 'http://localhost:5000',
      },
    },
  },
});
