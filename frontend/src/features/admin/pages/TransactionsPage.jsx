import { useData } from "@/shared/hooks/useData";
import { adminApi } from "@/features/admin/api/adminApi";
import { useState } from "react";
import { Heading } from "@/shared/components/PageHeading";
import { State } from "@/shared/components/AsyncState";
import { date, money } from "@/shared/utils/format";
import { Empty } from "@/shared/components/EmptyState";

export function Transactions() {
  const d = useData(adminApi.listPath("sepay-transactions"));
  const [query, setQuery] = useState("");
  return (
    <>
      <Heading title="Giao dịch SePay" eyebrow="JLer / ĐỐI SOÁT">
        <button onClick={d.reload}>Làm mới</button>
      </Heading>
      <input
        className="search-input"
        aria-label="Tìm giao dịch"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Tìm mã đơn, trạng thái hoặc nội dung…"
      />
      <State {...d} retry={d.reload} />
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>SePay ID / Thời gian</th>
              <th>Đơn / Nội dung</th>
              <th>Số tiền</th>
              <th>Kết quả đối soát</th>
            </tr>
          </thead>
          <tbody>
            {d.data
              ?.filter((t) =>
                JSON.stringify(t).toLowerCase().includes(query.toLowerCase()),
              )
              .map((t) => (
                <tr key={t.id}>
                  <td>
                    #{t.id}
                    <small>{date(t.receivedAt)}</small>
                  </td>
                  <td>
                    <b>{t.code || "Không có mã đơn"}</b>
                    <small>{t.content}</small>
                    <small>
                      {t.gateway} · {t.accountNumber}
                    </small>
                  </td>
                  <td>{money(t.amount)}</td>
                  <td>
                    <span
                      className={`tag ${t.status === "REVIEW" ? "red" : ""}`}
                    >
                      {t.status === "ACCEPTED"
                        ? "Đã xác nhận"
                        : t.status === "REVIEW"
                          ? "Cần kiểm tra"
                          : "Đã bỏ qua"}
                    </span>
                    <small>{t.reason}</small>
                  </td>
                </tr>
              ))}
          </tbody>
        </table>
        {d.data?.length === 0 && <Empty>Chưa nhận giao dịch SePay.</Empty>}
      </div>
      <p className="muted">
        Giao dịch cần kiểm tra không tự mở quyền học. Đối chiếu ngân hàng trước
        khi xác nhận thủ công tại mục Đơn hàng.
      </p>
    </>
  );
}
