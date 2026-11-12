import { useData } from "@/shared/hooks/useData";
import { adminApi } from "@/features/admin/api/adminApi";
import { useApp } from "@/shared/context/AppContext";
import { useState } from "react";
import { message } from "@/shared/utils/errors";
import { Heading } from "@/shared/components/PageHeading";
import { State } from "@/shared/components/AsyncState";
import { date, money } from "@/shared/utils/format";
import { statusText } from "@/shared/constants/orderStatus";
import { Empty } from "@/shared/components/EmptyState";

export function AdminOrders() {
  const d = useData(adminApi.listPath("orders"));
  const { notify } = useApp();
  const [filter, setFilter] = useState("");
  const [busy, setBusy] = useState(null);
  async function action(o, a) {
    if (
      !window.confirm(
        `${a === "MARK_PAID" ? "Xác nhận đã nhận đủ tiền cho" : a === "CANCEL" ? "Hủy" : "Cập nhật"} đơn ${o.code}?`,
      )
    )
      return;
    setBusy(o.id);
    try {
      await adminApi.orderAction(o.id, { action: a });
      d.reload();
      notify("Đã cập nhật đơn hàng");
    } catch (e) {
      notify(message(e), true);
    } finally {
      setBusy(null);
    }
  }
  return (
    <>
      <Heading title="Quản lý đơn hàng" eyebrow="JLer / VẬN HÀNH" />
      <input
        className="search-input"
        placeholder="Tìm mã đơn hoặc khách hàng…"
        aria-label="Tìm đơn hàng"
        value={filter}
        onChange={(e) => setFilter(e.target.value)}
      />
      <State {...d} retry={d.reload} />
      {d.data
        ?.filter((o) =>
          (o.code + " " + o.customer)
            .toLowerCase()
            .includes(filter.toLowerCase()),
        )
        .map((o) => (
          <article className="order-card" key={o.id}>
            <div className="order-heading">
              <div>
                <b>
                  {o.code} · {o.customer}
                </b>
                <p>{date(o.createdAt)}</p>
              </div>
              <span className="tag">
                {statusText[o.status]} · {statusText[o.paymentStatus]}
              </span>
            </div>
            {o.items.map((i) => (
              <p key={i.id}>
                {i.title} × {i.quantity} — {money(i.price * i.quantity)}
              </p>
            ))}
            {o.recipient && (
              <p className="muted">
                {o.recipient.fullName} · {o.recipient.phone} ·{" "}
                {o.recipient.address}
              </p>
            )}
            <p>
              Thanh toán: {o.paymentMethod} · Tổng <b>{money(o.total)}</b>
            </p>
            {o.note && <p>Ghi chú: {o.note}</p>}
            <div className="form-actions">
              {!["CANCELED", "COMPLETED"].includes(o.status) && (
                <>
                  {o.paymentStatus === "pending" && (
                    <button
                      disabled={busy === o.id}
                      onClick={() => action(o, "MARK_PAID")}
                    >
                      Xác nhận đã nhận tiền
                    </button>
                  )}
                  {o.status === "PENDING" && (
                    <>
                      <button
                        disabled={busy === o.id}
                        onClick={() => action(o, "CONFIRM")}
                      >
                        Xác nhận đơn
                      </button>
                      {o.paymentStatus === "pending" && (
                        <button
                          disabled={busy === o.id}
                          onClick={() => action(o, "CANCEL")}
                        >
                          Hủy đơn
                        </button>
                      )}
                    </>
                  )}
                  {o.status === "PREPARING" && o.recipient && (
                    <button
                      disabled={busy === o.id}
                      onClick={() => action(o, "SHIP")}
                    >
                      Bắt đầu giao
                    </button>
                  )}
                  {o.status === "SHIPPING" && o.paymentStatus === "paid" && (
                    <button
                      disabled={busy === o.id}
                      onClick={() => action(o, "COMPLETE")}
                    >
                      Hoàn thành
                    </button>
                  )}
                </>
              )}
            </div>
          </article>
        ))}
      {d.data?.length === 0 && <Empty>Chưa có đơn hàng.</Empty>}
    </>
  );
}
