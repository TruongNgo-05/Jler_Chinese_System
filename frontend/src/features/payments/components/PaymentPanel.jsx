import { usePaymentController } from "@/features/payments/hooks/usePaymentController";
import { money } from "@/shared/utils/format";
import { Link } from "react-router-dom";
import { Copy } from "@/shared/components/CopyButton";

export function Payment({ orderId, onPaid }) {
  const { data, error, setVersion, qrError, setQrError } = usePaymentController(
    { orderId, onPaid },
  );
  if (!data && !error)
    return <div className="state">Đang lấy thông tin thanh toán…</div>;
  return (
    <div className="payment-panel">
      {error && (
        <div className="info error" role="alert">
          Chưa kiểm tra được thanh toán: {error}
          <button onClick={() => setVersion((v) => v + 1)}>Thử lại</button>
        </div>
      )}
      {data &&
        (data.paymentStatus === "paid" ? (
          <div className="payment-success" role="status">
            <span>✓</span>
            <h2>Thanh toán thành công</h2>
            <p>
              Đã ghi nhận {money(data.amount)} cho đơn {data.code}.
            </p>
            <Link className="button" to="/my-courses">
              Vào khóa học của tôi →
            </Link>
          </div>
        ) : data.status === "CANCELED" ? (
          <div className="info">
            Đơn đã hủy. Vui lòng không chuyển tiền cho đơn này. Nếu đã chuyển,
            liên hệ JLer để được đối soát.
          </div>
        ) : data.available ? (
          <>
            <div className="payment-heading">
              <span className="tag">SePay · VietQR</span>
              <h3>Quét mã để thanh toán</h3>
              <p className="muted">
                Giữ nguyên số tiền và nội dung chuyển khoản.
              </p>
            </div>
            <div className="payment-qr">
              {!qrError ? (
                <img
                  src={data.qrUrl}
                  alt={`QR thanh toán đơn ${data.code}`}
                  onError={() => setQrError(true)}
                />
              ) : (
                <p>
                  Không tải được ảnh QR. Bạn có thể chuyển khoản bằng thông tin
                  bên dưới.
                </p>
              )}
            </div>
            <dl className="bank-details">
              <div>
                <dt>Ngân hàng</dt>
                <dd>{data.bank}</dd>
              </div>
              <div>
                <dt>Chủ tài khoản</dt>
                <dd>{data.accountName}</dd>
              </div>
              <div>
                <dt>Số tài khoản</dt>
                <dd>
                  {data.accountNumber}
                  <Copy value={data.accountNumber} />
                </dd>
              </div>
              <div>
                <dt>Số tiền</dt>
                <dd>{money(data.amount)}</dd>
              </div>
              <div>
                <dt>Nội dung</dt>
                <dd>
                  {data.transferContent}
                  <Copy value={data.transferContent} />
                </dd>
              </div>
            </dl>
            <div className="payment-waiting" role="status">
              <span className="spinner" />
              Đang chờ xác nhận từ ngân hàng…
            </div>
            <p className="muted">
              Trang sẽ cập nhật khi JLer nhận xác nhận thanh toán. Không cần bấm
              xác nhận đã chuyển tiền. Chuyển thiếu, thừa hoặc sai nội dung cần
              đối soát thủ công.
            </p>
          </>
        ) : (
          <div className="info">
            Thanh toán SePay chưa khả dụng cho đơn này. Vui lòng liên hệ JLer.
          </div>
        ))}
    </div>
  );
}
