import { useData } from "@/shared/hooks/useData";
import { examApi } from "@/features/exams/api/examApi";

export function usePracticeController() {
  const d = useData(examApi.quizzesPath);
  const history = useData(examApi.resultsPath);
  return { d, history };
}
