import { api } from "@/shared/api/http";

export const uploadApi = { image: (body) => api.post("/upload", body) };
