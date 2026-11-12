import { useCheckoutController } from "@/features/checkout/hooks/useCheckoutController";
import { Heading } from "@/shared/components/PageHeading";
import { State } from "@/shared/components/AsyncState";
import { Empty } from "@/shared/components/EmptyState";
import { money } from "@/shared/utils/format";

export function Checkout() {
  const {
    d,
    submit,
    physical,
    form,
    field,
    options,
    paymentMethod,
    totals,
    busy,
    apply,
  } = useCheckoutController();
  return (
    <section className="section">
      <Heading eyebrow="HOÀN TẤT LỰA CHỌN" title="Đặt hàng" />
      <State {...d} retry={d.reload} />
      {d.data?.items.length === 0 ? (
        <Empty>Giỏ hàng trống.</Empty>
      ) : (
        d.data && (
          <form className="checkout-grid" onSubmit={submit}>
            <div className="panel">
              <h2>
                {physical ? "Thông tin nhận hàng" : "Thông tin thanh toán"}
              </h2>
              {physical &&
                [
                  ["fullName", "Người nhận"],
                  ["phone", "Số điện thoại"],
                  ["address", "Địa chỉ đầy đủ"],
                ].map(([key, label]) => (
                  <label key={key}>
                    {label}
                    <input
                      required
                      maxLength={key === "phone" ? 15 : 255}
                      value={form[key]}
                      onChange={(e) => field(key, e.target.value)}
                    />
                  </label>
                ))}
              <State
                loading={options.loading}
                error={options.error}
                retry={options.reload}
              />
              <label>
                Phương thức thanh toán
                <select
                  value={paymentMethod}
                  onChange={(e) => field("paymentMethod", e.target.value)}
                >
                  {totals.total === 0 && <option value="FREE">Miễn phí</option>}
                  <option value="SEPAY" disabled={!options.data?.sepayEnabled}>
                    SePay – Quét QR chuyển khoản
                  </option>
                  {physical && (
                    <option value="COD">Thanh toán khi nhận sách (COD)</option>
                  )}
                </select>
              </label>
              <div className="info">
                {paymentMethod === "SEPAY"
                  ? options.data?.sepayEnabled
                    ? "Sau khi đặt hàng, quét QR SePay với số tiền và mã đơn được điền sẵn. Khóa học sẽ tự mở khi ngân hàng xác nhận thanh toán."
                    : "JLer chưa bật thanh toán SePay. Vui lòng liên hệ để được hỗ trợ."
                  : "Thanh toán khi nhận sách. Nếu đơn gồm khóa học, quyền học mở sau khi xác nhận đã thu tiền."}
              </div>
              <label>
                Ghi chú
                <textarea
                  maxLength="1000"
                  value={form.note}
                  onChange={(e) => field("note", e.target.value)}
                />
              </label>
            </div>
            <aside className="summary-box">
              <h2>Đơn hàng của bạn</h2>
              {d.data.items.map((i) => (
                <div key={i.id}>
                  <span>
                    {i.title} × {i.quantity}
                  </span>
                  <b>{money(i.price * i.quantity)}</b>
                </div>
              ))}
              <label>
                Mã giảm giá
                <div className="inline">
                  <input
                    value={form.voucherCode}
                    onChange={(e) => field("voucherCode", e.target.value)}
                    placeholder="Nhập mã"
                  />
                  <button type="button" disabled={busy} onClick={apply}>
                    Áp dụng
                  </button>
                </div>
              </label>
              <div>
                <span>Tạm tính</span>
                <b>{money(totals.subtotal)}</b>
              </div>
              <div>
                <span>Giảm giá</span>
                <b>−{money(totals.discount)}</b>
              </div>
              <div>
                <span>Giao hàng</span>
                <b>{money(totals.shippingFee)}</b>
              </div>
              <div className="total">
                <span>Tổng cộng</span>
                <b>{money(totals.total)}</b>
              </div>
              <p className="muted">
                Giá và tồn kho được kiểm tra lại khi đặt hàng.
              </p>
              <button
                className="button full"
                disabled={
                  busy ||
                  d.data.canCheckout === false ||
                  options.loading ||
                  !!options.error ||
                  (paymentMethod === "SEPAY" && !options.data?.sepayEnabled)
                }
              >
                {busy ? "Đang xử lý…" : "Xác nhận đặt hàng"}
              </button>
            </aside>
          </form>
        )
      )}
    </section>
  );
}
