import { useOrdersController } from "@/features/orders/hooks/useOrdersController";
import { Heading } from "@/shared/components/PageHeading";
import { State } from "@/shared/components/AsyncState";
import { Empty } from "@/shared/components/EmptyState";
import { date, money } from "@/shared/utils/format";
import { statusText } from "@/shared/constants/orderStatus";
import { Modal } from "@/shared/components/Modal";
import { Payment } from "@/features/payments/components/PaymentPanel";

export function Orders() {
  const { d, setParams, busy, cancel, selected } = useOrdersController();
  return (
    <section className="section">
      <Heading eyebrow="THEO DÕI HÀNH TRÌNH" title="Đơn hàng của tôi" />
      <State {...d} retry={d.reload} />
      {d.data?.length === 0 && <Empty>Bạn chưa có đơn hàng.</Empty>}
      <div className="order-list">
        {d.data?.map((o) => (
          <article className="order-card" key={o.id}>
            <div className="order-heading">
              <div>
                <b>{o.code}</b>
                <p className="muted">{date(o.createdAt)}</p>
              </div>
              <div>
                <span className={`tag ${o.status === "CANCELED" ? "red" : ""}`}>
                  {statusText[o.status]}
                </span>
                <span className="tag neutral">
                  {statusText[o.paymentStatus]}
                </span>
              </div>
            </div>
            {o.items.map((i) => (
              <div className="order-line" key={i.id}>
                <span>
                  {i.title} × {i.quantity}
                </span>
                <strong>{money(i.price * i.quantity)}</strong>
              </div>
            ))}
            {o.recipient && (
              <p className="muted">
                Giao tới: {o.recipient.fullName} · {o.recipient.phone}
                <br />
                {o.recipient.address}
              </p>
            )}
            {o.paymentMethod === "BANK_TRANSFER" &&
              o.paymentStatus === "pending" &&
              o.status !== "CANCELED" && (
                <div className="info">
                  Liên hệ đơn vị vận hành JLer để nhận thông tin chuyển khoản.
                  Nội dung chuyển khoản: <b>{o.code}</b>. Quyền học được mở sau
                  khi xác nhận đã nhận tiền.
                </div>
              )}
            {o.paymentMethod === "SEPAY" &&
              o.paymentStatus === "pending" &&
              o.status !== "CANCELED" && (
                <div className="sepay-order-actions">
                  <span className="muted">
                    Thanh toán qua SePay · Tự động mở quyền học
                  </span>
                  <button
                    className="button small"
                    onClick={() => setParams({ pay: String(o.id) })}
                  >
                    Thanh toán QR
                  </button>
                </div>
              )}
            <div className="order-bottom">
              <span>
                Tạm tính {money(o.subtotal)} · Giảm {money(o.discount)} · Phí
                giao {money(o.shippingFee)}
              </span>
              <strong>Tổng: {money(o.total)}</strong>
              {o.status === "PENDING" && o.paymentStatus === "pending" && (
                <button disabled={busy === o.id} onClick={() => cancel(o.id)}>
                  Hủy đơn
                </button>
              )}
            </div>
          </article>
        ))}
      </div>
      {selected && (
        <Modal
          title="Thanh toán SePay"
          close={() => setParams({}, { replace: true })}
        >
          <Payment key={selected} orderId={selected} onPaid={d.reload} />
        </Modal>
      )}
    </section>
  );
}
