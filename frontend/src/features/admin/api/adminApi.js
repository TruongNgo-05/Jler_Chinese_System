import { api } from "@/shared/api/http";

export const adminApi = {
  listPath: (kind) => `/platform/admin/${kind}`,
  reportsPath: "/platform/admin/reports",
  reportPath: (range) =>
    `/platform/admin/reports?${new URLSearchParams(range)}`,
  save: (kind, id, body) =>
    api[id ? "put" : "post"](
      `/platform/admin/${kind}${id ? "/" + id : ""}`,
      body,
    ),
  remove: (kind, id) => api.delete(`/platform/admin/${kind}/${id}`),
  orderAction: (id, body) =>
    api.post(`/platform/admin/orders/${id}/actions`, body),
  quizVisibility: (id, body) =>
    api.put(`/platform/admin/quizzes/${id}/visibility`, body),
  duplicateQuiz: (id) => api.post(`/platform/admin/quizzes/${id}/duplicate`),
};
