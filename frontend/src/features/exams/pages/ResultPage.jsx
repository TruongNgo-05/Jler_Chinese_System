import { useParams, Link } from "react-router-dom";
import { useData } from "@/shared/hooks/useData";
import { examApi } from "@/features/exams/api/examApi";
import { State } from "@/shared/components/AsyncState";
import { Heading } from "@/shared/components/PageHeading";

export function Result() {
  const { id } = useParams();
  const d = useData(examApi.resultPath(id));
  const r = d.data;
  return (
    <section className="section">
      <State {...d} retry={d.reload} />
      {r && (
        <>
          <Heading title={r.title} eyebrow="KẾT QUẢ LÀM BÀI" />
          <div className="result-summary">
            <strong>
              {r.score}
              <small>/100</small>
            </strong>
            <div>
              <h2>
                {r.status === "PASSED"
                  ? "Bạn đã đạt!"
                  : "Tiếp tục luyện tập nhé."}
              </h2>
              <p>
                Đúng {r.correct}/{r.total} câu · {r.timeSpent} giây
              </p>
              <Link to="/practice">Quay lại luyện thi →</Link>
            </div>
          </div>
          {r.details.map((a, i) => (
            <div className="panel result-question" key={a.question.id}>
              <h3>
                {i + 1}. {a.question.content}
              </h3>
              {a.question.options.map((o) => (
                <p
                  className={
                    o.correct
                      ? "answer-correct"
                      : a.selected === o.id
                        ? "answer-wrong"
                        : ""
                  }
                  key={o.id}
                >
                  {o.correct ? "✓ " : a.selected === o.id ? "✗ " : ""}
                  {o.content}
                  {a.selected === o.id ? " (Bạn chọn)" : ""}
                </p>
              ))}
            </div>
          ))}
        </>
      )}
    </section>
  );
}
