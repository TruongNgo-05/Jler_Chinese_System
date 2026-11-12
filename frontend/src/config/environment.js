export const API_BASE_URL = (import.meta.env.VITE_API_URL || "/api").replace(
  /\/+$/,
  "",
);
export const BACKEND_ORIGIN = API_BASE_URL.replace(/\/api$/, "");
