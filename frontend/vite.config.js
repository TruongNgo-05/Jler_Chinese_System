import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { fileURLToPath, URL } from "node:url";
const apiProxy = {
  target: "http://127.0.0.1:8080",
  changeOrigin: true,
  configure(proxy) {
    // Local development requests are same-origin to Vite. Do not forward the
    // browser's frontend Origin as a cross-origin request to the internal backend.
    proxy.on("proxyReq", (request) => request.removeHeader("origin"));
  },
};
export default defineConfig({
  plugins: [react()],
  resolve: { alias: { "@": fileURLToPath(new URL("./src", import.meta.url)) } },
  server: { proxy: { "/api": apiProxy, "/uploads": apiProxy } },
});
