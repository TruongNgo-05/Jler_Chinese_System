import { api } from "@/shared/api/http";

export const learningApi = {
  myCoursesPath: "/platform/my-courses",
  learnPath: (id) => `/platform/learn/${id}`,
  lessonPath: (id) => `/platform/lessons/${id}`,
  cardsPath: (id) => `/platform/lessons/${id}/flashcards`,
  commentsPath: (id) => `/platform/lessons/${id}/comments`,
  progress: (id, body) => api.put(`/platform/lessons/${id}/progress`, body),
  comment: (id, body) => api.post(`/platform/lessons/${id}/comments`, body),
  deleteComment: (id) => api.delete(`/platform/comments/${id}`),
};
