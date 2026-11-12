import { BACKEND_ORIGIN } from "@/config/environment";

export const media = (url) =>
  !url ? "" : url.startsWith("/") ? BACKEND_ORIGIN + url : url;
