import { api } from "@/shared/api/http";

export const vocabularyApi = {
  path: "/platform/words",
  add: (body) => api.post("/platform/words", body),
  remove: (id) => api.delete(`/platform/words/${id}`),
  update: (id, body) => api.put(`/platform/words/${id}`, body),
};
