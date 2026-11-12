import { useDetailController } from "@/features/catalog/hooks/useDetailController";
import { Link } from "react-router-dom";
import { State } from "@/shared/components/AsyncState";
import { Image } from "@/shared/components/MediaImage";
import { money } from "@/shared/utils/format";

export function Detail({ kind }) {
  const {
    d,
    p,
    selectedImage,
    id,
    qty,
    setQty,
    busy,
    action,
    setSelectedImage,
  } = useDetailController({ kind });
  return (
    <section className="section">
      <Link className="text-link" to={`/${kind}`}>
        ← Quay lại {kind === "books" ? "hiệu sách" : "khóa học"}
      </Link>
      <State {...d} retry={d.reload} />
      {p && (
        <>
          <div className="detail-grid">
            <div className="detail-art">
              <Image
                src={selectedImage?.id === id ? selectedImage.src : p.imageUrl}
                alt={p.title}
              />
            </div>
            <div>
              <p className="eyebrow">
                {kind === "books" ? "JLer BOOKS" : p.courseType}
              </p>
              <h1>{p.title}</h1>
              <div className="price large">
                {money(p.discountPrice ?? p.price)}
                {p.discountPrice != null && <del>{money(p.price)}</del>}
              </div>
              <p className="pre-line description">{p.description}</p>
              {kind === "books" ? (
                <p>
                  {p.stock > 0 ? `Còn ${p.stock} sản phẩm` : "Tạm hết hàng"}
                </p>
              ) : (
                <p>{p.chapterCount} chương · Học trên tài khoản của bạn</p>
              )}
              <div className="detail-actions">
                {kind === "books" && (
                  <input
                    aria-label="Số lượng"
                    type="number"
                    min="1"
                    max={Math.min(99, p.stock || 1)}
                    value={qty}
                    onChange={(e) => setQty(Number(e.target.value))}
                  />
                )}
                <button
                  className="button"
                  disabled={busy || (kind === "books" && p.stock === 0)}
                  onClick={() => action()}
                >
                  Thêm vào giỏ hàng
                </button>
                <button
                  className="button secondary"
                  disabled={busy}
                  onClick={() => action(true)}
                >
                  ♡ Lưu lại
                </button>
              </div>
              {kind === "courses" && (
                <p className="muted">
                  Khóa học tự mở khi SePay xác nhận thanh toán thành công.{" "}
                  <Link to="/my-courses">Khóa học của tôi →</Link>
                </p>
              )}
            </div>
          </div>
          {kind === "books" && p.images?.length > 1 && (
            <div className="gallery">
              {p.images.map((url, i) => (
                <button
                  key={i}
                  className="gallery-thumb"
                  aria-label={`Xem ảnh ${i + 1}`}
                  onClick={() => setSelectedImage({ id, src: url })}
                >
                  <Image src={url} alt={`${p.title} – ảnh ${i + 1}`} />
                </button>
              ))}
            </div>
          )}
          {p.chapters && (
            <div className="curriculum">
              <h2>Nội dung khóa học</h2>
              {p.chapters.map((ch) => (
                <details key={ch.id} open>
                  <summary>{ch.title}</summary>
                  {ch.contents.map((c) => (
                    <div className="lesson-row" key={c.id}>
                      <span>
                        {c.type === "LESSON" ? "▷" : "✎"}{" "}
                        {c.lesson?.title || c.quiz?.title}
                      </span>
                      {c.lesson?.isFree ? (
                        <Link className="tag" to={`/preview/${c.lesson.id}`}>
                          Học thử →
                        </Link>
                      ) : (
                        <span className="muted">Trong khóa học</span>
                      )}
                    </div>
                  ))}
                </details>
              ))}
            </div>
          )}
        </>
      )}
    </section>
  );
}
