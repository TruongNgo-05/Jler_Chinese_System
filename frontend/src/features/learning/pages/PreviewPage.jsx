import { useParams } from "react-router-dom";
import { useData } from "@/shared/hooks/useData";
import { learningApi } from "@/features/learning/api/learningApi";
import { Heading } from "@/shared/components/PageHeading";
import { State } from "@/shared/components/AsyncState";
import { Lesson } from "@/features/learning/components/LessonPanel";

export function Preview() {
  const { id } = useParams();
  const d = useData(learningApi.lessonPath(id));
  return (
    <section className="section">
      <Heading title="Bài học thử" eyebrow="BẮT ĐẦU TỪ MỘT BƯỚC NHỎ" />
      <State {...d} retry={d.reload} />
      {d.data && (
        <Lesson lesson={d.data} completed={false} onComplete={() => {}} />
      )}
    </section>
  );
}
