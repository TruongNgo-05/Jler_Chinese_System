import { useReportsController } from "@/features/admin/hooks/useReportsController";
import { Heading } from "@/shared/components/PageHeading";
import { adminApi } from "@/features/admin/api/adminApi";
import { State } from "@/shared/components/AsyncState";
import { money } from "@/shared/utils/format";
import { Empty } from "@/shared/components/EmptyState";

export function Reports() {
  const { setPath, range, setRange, d, report } = useReportsController();
  return (
    <>
      <Heading title="Báo cáo doanh thu" eyebrow="JLer / KINH DOANH" />
      <form
        className="filters"
        onSubmit={(e) => {
          e.preventDefault();
          setPath(adminApi.reportPath(range));
        }}
      >
        <label>
          Từ ngày
          <input
            type="date"
            value={range.from}
            onChange={(e) => setRange({ ...range, from: e.target.value })}
          />
        </label>
        <label>
          Đến ngày
          <input
            type="date"
            value={range.to}
            onChange={(e) => setRange({ ...range, to: e.target.value })}
          />
        </label>
        <button className="button">Xem báo cáo</button>
        <button type="button" onClick={d.reload}>
          Làm mới
        </button>
      </form>
      <State {...d} retry={d.reload} />
      {report && (
        <>
          <div className="report-stats">
            <article>
              <span>Doanh thu đã thu</span>
              <strong>{money(report.revenue)}</strong>
            </article>
            <article>
              <span>Đơn đã thanh toán</span>
              <strong>{report.paidOrders}</strong>
            </article>
          </div>
          <p className="muted">
            Theo ngày thanh toán, bao gồm phí giao hàng và đã trừ giảm giá. Mặc
            định 30 ngày gần nhất.
          </p>
          {report.unknownPaymentDates > 0 && (
            <p className="info">
              Có {report.unknownPaymentDates} đơn thiếu ngày thanh toán, chưa
              được tính trong khoảng ngày.
            </p>
          )}
          <div className="report-grid">
            <div className="panel">
              <h2>Doanh thu theo ngày</h2>
              <div className="revenue-bars">
                {report.daily?.map((day) => (
                  <div className="revenue-row" key={day.date}>
                    <span>{day.date}</span>
                    <div>
                      <i
                        style={{
                          width: `${Math.max(0, (Number(day.revenue) / Math.max(1, ...report.daily.map((d) => Number(d.revenue)))) * 100)}%`,
                        }}
                      />
                    </div>
                    <b>{money(day.revenue)}</b>
                  </div>
                ))}
              </div>
            </div>
            <div className="panel">
              <h2>Sản phẩm bán chạy</h2>
              {report.topItems?.length === 0 ? (
                <Empty>Chưa có đơn đã thanh toán trong thời gian này.</Empty>
              ) : (
                report.topItems?.map((item, i) => (
                  <div className="report-product" key={i}>
                    <b>
                      {i + 1}. {item.title}
                    </b>
                    <span>
                      {item.quantity}{" "}
                      {item.type === "Book" ? "cuốn" : "quyền học"}
                    </span>
                  </div>
                ))
              )}
            </div>
          </div>
        </>
      )}
    </>
  );
}
