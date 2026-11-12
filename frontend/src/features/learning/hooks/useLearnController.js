import { useParams } from "react-router-dom";
import { useData } from "@/shared/hooks/useData";
import { learningApi } from "@/features/learning/api/learningApi";
import { useState } from "react";

export function useLearnController() {
  const { id } = useParams();
  const d = useData(learningApi.learnPath(id));
  const [selected, setSelected] = useState(null);
  const lessons = d.data?.chapters.flatMap((ch) => ch.contents) || [];
  const current = lessons.find((x) => x.id === selected) || lessons[0];
  const completed =
    d.data?.progress.filter((p) => p.completed).map((p) => p.lessonId) || [];
  return { d, current, setSelected, completed };
}
