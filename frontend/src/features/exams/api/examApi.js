import { api } from "@/shared/api/http";

export const examApi = {
  quizzesPath: "/platform/catalog/quizzes",
  resultsPath: "/platform/results",
  resultPath: (id) => `/platform/results/${id}`,
  start: (id) => api.post(`/platform/quizzes/${id}/start`),
  submit: (id, body) => api.post(`/platform/quiz-sessions/${id}/submit`, body),
};
