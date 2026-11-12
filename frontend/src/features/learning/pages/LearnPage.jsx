import { useLearnController } from "@/features/learning/hooks/useLearnController";
import { Link } from "react-router-dom";
import { State } from "@/shared/components/AsyncState";
import { Heading } from "@/shared/components/PageHeading";
import { Lesson } from "@/features/learning/components/LessonPanel";
import { Empty } from "@/shared/components/EmptyState";

export function Learn() {
  const { d, current, setSelected, completed } = useLearnController();
  return (
    <section className="section">
      <Link to="/my-courses">← Khóa học của tôi</Link>
      <State {...d} retry={d.reload} />
      {d.data && (
        <>
          <Heading title={d.data.title} eyebrow="KHÔNG GIAN HỌC TẬP" />
          <div className="learning-grid">
            <aside className="curriculum">
              <h3>Nội dung khóa học</h3>
              {d.data.chapters.map((ch) => (
                <details open key={ch.id}>
                  <summary>{ch.title}</summary>
                  {ch.contents.map((c) => (
                    <button
                      key={c.id}
                      className={`curriculum-button ${current?.id === c.id ? "active" : ""}`}
                      onClick={() => setSelected(c.id)}
                    >
                      <span>
                        {c.lesson && completed.includes(c.lesson.id)
                          ? "✓"
                          : "○"}
                      </span>
                      {c.lesson?.title || c.quiz?.title}
                    </button>
                  ))}
                </details>
              ))}
            </aside>
            {current?.lesson ? (
              <Lesson
                key={current.lesson.id}
                lesson={current.lesson}
                completed={completed.includes(current.lesson.id)}
                onComplete={d.reload}
                position={
                  d.data.progress.find((p) => p.lessonId === current.lesson.id)
                    ?.lastPosition || 0
                }
              />
            ) : current?.quiz ? (
              <div className="panel">
                <h2>{current.quiz.title}</h2>
                <p>
                  {current.quiz.timeLimit} phút · Điểm đạt{" "}
                  {current.quiz.passScore}/100
                </p>
                <Link className="button" to={`/exam/${current.quiz.id}`}>
                  Bắt đầu làm bài
                </Link>
              </div>
            ) : (
              <Empty>Khóa học chưa có nội dung.</Empty>
            )}
          </div>
        </>
      )}
    </section>
  );
}
