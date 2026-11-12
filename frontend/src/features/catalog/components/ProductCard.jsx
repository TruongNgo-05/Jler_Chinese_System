import { useApp } from "@/shared/context/AppContext";
import { useState } from "react";
import { saveSelection } from "@/features/catalog/api/productActions";
import { cartChanged } from "@/shared/events/cartEvents";
import { message } from "@/shared/utils/errors";
import { Link } from "react-router-dom";
import { Image } from "@/shared/components/MediaImage";
import { money } from "@/shared/utils/format";

export function ProductCard({ item, kind = "books" }) {
  const { user, notify } = useApp();
  const [busy, setBusy] = useState(false);
  async function add(favorite = false) {
    if (!user) {
      notify("Vui lòng đăng nhập để tiếp tục.");
      return;
    }
    setBusy(true);
    try {
      await saveSelection(favorite, {
        itemId: item.id,
        itemType: kind === "books" ? "Book" : "Course",
      });
      if (!favorite) cartChanged();
      notify(favorite ? "Đã cập nhật yêu thích" : "Đã thêm vào giỏ hàng");
    } catch (e) {
      notify(message(e), true);
    } finally {
      setBusy(false);
    }
  }
  return (
    <article className="product-card">
      <Link className="product-image" to={`/${kind}/${item.id}`}>
        <Image src={item.imageUrl} alt={item.title} />
        <span className="image-label">
          {kind === "books" ? "JLer BOOKS" : "JLer ACADEMY"}
        </span>
      </Link>
      <div className="product-body">
        <div className="eyebrow">
          {kind === "books"
            ? item.categories?.join(" · ") || "Giáo trình"
            : item.courseType}
        </div>
        <Link to={`/${kind}/${item.id}`} className="product-title">
          {item.title}
        </Link>
        <div className="price">
          {money(item.discountPrice ?? item.price)}
          {item.discountPrice != null && <del>{money(item.price)}</del>}
        </div>
        <div className="card-actions">
          <button
            className="button small"
            disabled={busy || (kind === "books" && item.stock === 0)}
            onClick={() => add()}
          >
            {kind === "books" && item.stock === 0
              ? "Tạm hết hàng"
              : "Thêm vào giỏ"}
          </button>
          <button
            className="icon-button"
            aria-label="Yêu thích"
            disabled={busy}
            onClick={() => add(true)}
          >
            ♡
          </button>
        </div>
      </div>
    </article>
  );
}
