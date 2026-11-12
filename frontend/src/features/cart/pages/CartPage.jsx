import { useCartController } from "@/features/cart/hooks/useCartController";
import { Heading } from "@/shared/components/PageHeading";
import { State } from "@/shared/components/AsyncState";
import { Empty } from "@/shared/components/EmptyState";
import { Link } from "react-router-dom";
import { Image } from "@/shared/components/MediaImage";
import { money } from "@/shared/utils/format";

export function Cart() {
  const { d, busy, update } = useCartController();
  return (
    <section className="section">
      <Heading eyebrow="LỰA CHỌN CỦA BẠN" title="Giỏ hàng" />
      <State {...d} retry={d.reload} />
      {d.data?.items.length === 0 ? (
        <Empty>
          Giỏ hàng đang trống. <Link to="/books">Khám phá hiệu sách →</Link>
        </Empty>
      ) : (
        d.data && (
          <div className="checkout-grid">
            <div className="cart-items">
              {d.data.items.map((i) => (
                <article className="cart-item" key={i.id}>
                  <Image src={i.imageUrl} alt={i.title} />
                  <div>
                    <Link
                      to={`/${i.itemType === "Book" ? "books" : "courses"}/${i.itemId}`}
                    >
                      <h3>{i.title}</h3>
                    </Link>
                    <span className="muted">
                      {i.itemType === "Book" ? "Sách" : "Khóa học"}
                    </span>
                    <p>{money(i.price)}</p>
                  </div>
                  {i.itemType === "Book" ? (
                    <div className="quantity">
                      <button
                        aria-label="Giảm số lượng"
                        disabled={
                          busy || i.itemType === "Course" || i.quantity === 1
                        }
                        onClick={() => update(i.id, -1)}
                      >
                        −
                      </button>
                      <span>{i.quantity}</span>
                      <button
                        aria-label="Tăng số lượng"
                        disabled={
                          busy ||
                          i.available === false ||
                          i.quantity >= (i.maxQuantity ?? 99)
                        }
                        onClick={() => update(i.id, 1)}
                      >
                        +
                      </button>
                    </div>
                  ) : (
                    <span className="license-tag">1 quyền truy cập</span>
                  )}
                  <div className="cart-line-total">
                    <strong>
                      {money(i.lineTotal ?? i.price * i.quantity)}
                    </strong>
                    {i.availabilityReason && (
                      <small className="error">{i.availabilityReason}</small>
                    )}
                  </div>
                  <button
                    className="link-button"
                    disabled={busy}
                    onClick={() => update(i.id, 0)}
                  >
                    Xóa
                  </button>
                </article>
              ))}
            </div>
            <aside className="summary-box">
              <h2>Tóm tắt giỏ hàng</h2>
              <div>
                <span>Tạm tính</span>
                <strong>{money(d.data.subtotal)}</strong>
              </div>
              <p className="muted">
                Phí giao sách: 30.000đ/đơn. Khóa học không tính phí giao hàng.
              </p>
              {d.data.canCheckout === false ? (
                <p className="error">
                  Kiểm tra các sản phẩm không khả dụng trước khi đặt hàng.
                </p>
              ) : (
                <Link className="button full" to="/checkout">
                  Tiến hành đặt hàng →
                </Link>
              )}
              <Link className="continue" to="/books">
                Tiếp tục mua sắm
              </Link>
            </aside>
          </div>
        )
      )}
    </section>
  );
}
