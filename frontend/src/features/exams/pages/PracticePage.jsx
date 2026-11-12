import { usePracticeController } from "@/features/exams/hooks/usePracticeController";
import { Heading } from "@/shared/components/PageHeading";
import { State } from "@/shared/components/AsyncState";
import { Link } from "react-router-dom";
import { Empty } from "@/shared/components/EmptyState";
import { date } from "@/shared/utils/format";

export function Practice() {
  const { d, history } = usePracticeController();
  return (
    <section className="section">
      <Heading
        title="Luyện thi trắc nghiệm"
        eyebrow="MỖI LẦN THỬ, MỘT BƯỚC TIẾN"
      />
      <State {...d} retry={d.reload} />
      <div className="quiz-grid">
        {d.data?.map((q) => (
          <article className="panel" key={q.id}>
            <span className="tag">{q.quizType || "Luyện tập"}</span>
            <h2>{q.title}</h2>
            <p>
              {q.questionCount} câu · {q.timeLimit} phút
            </p>
            <p className="muted">
              Điểm đạt: {q.passScore}/100 ·{" "}
              {q.maxAttempts ? `${q.maxAttempts} lượt` : "Không giới hạn lượt"}
            </p>
            <Link className="button small" to={`/exam/${q.id}`}>
              Bắt đầu →
            </Link>
          </article>
        ))}
      </div>
      {d.data?.length === 0 && <Empty>Chưa có đề luyện tập.</Empty>}
      <h2 className="section-title">Lịch sử làm bài</h2>
      {history.data?.map((r) => (
        <Link className="history-row" key={r.id} to={`/results/${r.id}`}>
          <span>
            <b>{r.title}</b>
            <small>{date(r.submittedAt)}</small>
          </span>
          <strong>{r.score}/100 →</strong>
        </Link>
      ))}
      {history.data?.length === 0 && <p className="muted">Chưa có kết quả.</p>}
    </section>
  );
}
