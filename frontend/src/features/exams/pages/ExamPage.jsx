import { useExamController } from "@/features/exams/hooks/useExamController";
import { Link } from "react-router-dom";

export function Exam() {
  const {
    exam,
    error,
    busy,
    start,
    answers,
    seconds,
    expired,
    select,
    submit,
  } = useExamController();
  return (
    <section className="section exam-page">
      <Link to="/practice">← Luyện thi</Link>
      {!exam ? (
        <div className="panel start-exam">
          <p className="eyebrow">SẴN SÀNG THỬ SỨC</p>
          <h1>Bắt đầu lượt làm bài</h1>
          <p>
            Thời gian tính từ lúc bắt đầu. Mỗi lượt chỉ nộp một lần. Khi hết
            giờ, bài sẽ tự nộp.
          </p>
          <p className="muted">
            Tải lại trang rồi bấm tiếp tục sẽ khôi phục lượt đang làm; thời gian
            vẫn tiếp tục chạy.
          </p>
          {error && <p className="error">{error}</p>}
          <button className="button" disabled={busy} onClick={start}>
            {busy ? "Đang mở đề…" : "Bắt đầu / Tiếp tục lượt đang làm"}
          </button>
        </div>
      ) : (
        <>
          <div className="exam-bar">
            <div>
              <h1>{exam.title}</h1>
              <small>
                {Object.keys(answers).length}/{exam.questions.length} câu đã
                chọn
              </small>
            </div>
            <strong className={seconds < 60 ? "error" : ""}>
              {String(Math.floor(seconds / 60)).padStart(2, "0")}:
              {String(seconds % 60).padStart(2, "0")}
            </strong>
          </div>
          {exam.questions.map((q, index) => (
            <fieldset className="question" key={q.id}>
              <legend>
                Câu {index + 1}. {q.content}
              </legend>
              {q.audioUrl && <audio src={q.audioUrl} controls />}
              {q.options.map((o) => (
                <label
                  className={answers[q.id] === o.id ? "selected" : ""}
                  key={o.id}
                >
                  <input
                    type="radio"
                    name={`q-${q.id}`}
                    checked={answers[q.id] === o.id}
                    disabled={!!expired || busy}
                    onChange={() => select(q.id, o.id)}
                  />
                  {o.content}
                </label>
              ))}
            </fieldset>
          ))}
          <button
            className="button"
            disabled={busy}
            onClick={() => {
              if (window.confirm("Nộp bài và kết thúc lượt thi?")) submit();
            }}
          >
            {busy ? "Đang nộp…" : "Nộp bài"}
          </button>
        </>
      )}
    </section>
  );
}
