import { useVocabularyController } from "@/features/vocabulary/hooks/useVocabularyController";
import { Heading } from "@/shared/components/PageHeading";
import { State } from "@/shared/components/AsyncState";
import { Empty } from "@/shared/components/EmptyState";

export function Vocabulary() {
  const { save, form, setForm, busy, filter, setFilter, d, update } =
    useVocabularyController();
  return (
    <section className="section">
      <Heading title="Sổ từ vựng của tôi" eyebrow="GÓP NHẶT MỖI NGÀY" />
      <form className="panel vocabulary-form" onSubmit={save}>
        <label>
          Từ / Pinyin
          <input
            required
            maxLength="255"
            value={form.front}
            onChange={(e) => setForm({ ...form, front: e.target.value })}
          />
        </label>
        <label>
          Nghĩa tiếng Việt
          <input
            required
            maxLength="255"
            value={form.back}
            onChange={(e) => setForm({ ...form, back: e.target.value })}
          />
        </label>
        <button className="button" disabled={busy}>
          + Lưu từ
        </button>
      </form>
      <input
        className="search-input"
        aria-label="Tìm từ vựng"
        placeholder="Tìm trong sổ từ…"
        value={filter}
        onChange={(e) => setFilter(e.target.value)}
      />
      <State {...d} retry={d.reload} />
      <div className="word-grid">
        {d.data
          ?.filter((w) =>
            (w.front + " " + w.back)
              .toLowerCase()
              .includes(filter.toLowerCase()),
          )
          .map((w) => (
            <article className="panel" key={w.id}>
              <p className="eyebrow">
                {w.mastered ? "✓ ĐÃ THUỘC" : "ĐANG ÔN TẬP"}
              </p>
              <h2>{w.front}</h2>
              <p>{w.back}</p>
              <div className="form-actions">
                <button disabled={busy} onClick={() => update(w)}>
                  {w.mastered ? "Ôn lại" : "Đánh dấu đã thuộc"}
                </button>
                <button disabled={busy} onClick={() => update(w, true)}>
                  Xóa
                </button>
              </div>
            </article>
          ))}
      </div>
      {d.data?.length === 0 && (
        <Empty>Lưu từ đầu tiên của bạn ở phía trên.</Empty>
      )}
    </section>
  );
}
