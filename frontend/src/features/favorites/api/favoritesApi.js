import { api } from "@/shared/api/http";

export const favoritesApi = {
  path: "/platform/favorites",
  toggle: (body) => api.post("/platform/favorites", body),
};
