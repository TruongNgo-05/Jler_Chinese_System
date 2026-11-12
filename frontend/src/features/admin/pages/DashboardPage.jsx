import { useData } from "@/shared/hooks/useData";
import { adminApi } from "@/features/admin/api/adminApi";
import { Heading } from "@/shared/components/PageHeading";
import { State } from "@/shared/components/AsyncState";
import { money } from "@/shared/utils/format";

export function Dashboard() {
  const d = useData(adminApi.listPath("dashboard"));
  return (
    <>
      <Heading title="Tổng quan" eyebrow="JLer / QUẢN TRỊ" />
      <State {...d} retry={d.reload} />
      {d.data && (
        <>
          <div className="stats-grid">
            {[
              ["Doanh thu đã thu", money(d.data.revenue)],
              ["Đơn hàng", d.data.orders],
              ["Chờ xử lý", d.data.pendingOrders],
              ["Tài khoản", d.data.users],
              ["Đầu sách", d.data.books],
              ["Khóa học", d.data.courses],
            ].map(([label, value]) => (
              <div className="stat" key={label}>
                <span>{label}</span>
                <strong>{value}</strong>
              </div>
            ))}
          </div>
          <div className="panel">
            <h2>Sách sắp hết hàng</h2>
            {d.data.lowStock.map((b) => (
              <p key={b.id}>
                {b.title} <b>· Còn {b.stock}</b>
              </p>
            ))}
            {!d.data.lowStock.length && (
              <p className="muted">Tồn kho đang ổn định.</p>
            )}
          </div>
        </>
      )}
    </>
  );
}
