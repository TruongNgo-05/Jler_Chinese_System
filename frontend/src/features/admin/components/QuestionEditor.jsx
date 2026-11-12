export function Questions({ value = [], onChange }) {
  function edit(index, key, v) {
    onChange(value.map((q, i) => (i === index ? { ...q, [key]: v } : q)));
  }
  return (
    <div className="questions-editor">
      {value.map((q, i) => (
        <div className="question-editor" key={i}>
          <div className="inline">
            <b>Câu {i + 1}</b>
            <button
              type="button"
              onClick={() => onChange(value.filter((_, n) => n !== i))}
            >
              Xóa câu
            </button>
          </div>
          <textarea
            aria-label={`Nội dung câu ${i + 1}`}
            required
            value={q.content}
            onChange={(e) => edit(i, "content", e.target.value)}
          />
          {q.options.map((o, n) => (
            <div className="inline" key={n}>
              <input
                type="radio"
                aria-label={`Đáp án đúng ${n + 1} câu ${i + 1}`}
                name={`correct-${i}`}
                checked={!!o.correct}
                onChange={() =>
                  edit(
                    i,
                    "options",
                    q.options.map((p, k) => ({ ...p, correct: k === n })),
                  )
                }
              />
              <input
                required
                aria-label={`Đáp án ${n + 1} câu ${i + 1}`}
                value={o.content}
                onChange={(e) =>
                  edit(
                    i,
                    "options",
                    q.options.map((p, k) =>
                      k === n ? { ...p, content: e.target.value } : p,
                    ),
                  )
                }
              />
            </div>
          ))}
        </div>
      ))}
      <button
        type="button"
        onClick={() =>
          onChange([
            ...value,
            {
              content: "",
              options: Array.from({ length: 4 }, (_, i) => ({
                content: "",
                correct: i === 0,
              })),
            },
          ])
        }
      >
        + Thêm câu hỏi
      </button>
      <p className="muted">
        Mỗi câu chọn đúng một đáp án. Đề đã có lượt làm sẽ được giữ nguyên để
        bảo toàn kết quả.
      </p>
    </div>
  );
}
