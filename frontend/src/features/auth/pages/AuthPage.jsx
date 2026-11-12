import { useAuthController } from "@/features/auth/hooks/useAuthController";
import { Navigate, Link } from "react-router-dom";

export function Auth({ mode = "login" }) {
  const { user, titles, success, submit, fields, form, setForm, error, busy } =
    useAuthController({ mode });
  if (mode === "login" && user)
    return (
      <Navigate to={user.role === "ADMIN" ? "/admin/dashboard" : "/"} replace />
    );
  return (
    <div className="auth-wrap">
      <section className="auth-art">
        <span className="eyebrow">JLer · CHINESE LEARNING</span>
        <div>
          你好<span>Rất vui được gặp bạn.</span>
        </div>
        <p>
          Một ngôn ngữ mới.
          <br />
          Một thế giới đang chờ.
        </p>
      </section>
      <section className="auth-panel">
        <Link to="/">← Về trang chủ</Link>
        <h1>{titles[mode]}</h1>
        <p className="muted">
          {mode === "login"
            ? "Tiếp tục hành trình học tiếng Trung của bạn."
            : "Chúng tôi luôn đồng hành cùng bạn."}
        </p>
        {success ? (
          <div className="success" role="status">
            {success}
            <p>
              <Link className="button" to="/login">
                Đăng nhập
              </Link>
            </p>
          </div>
        ) : (
          <form onSubmit={submit}>
            {fields.map(([key, label, type]) => (
              <label key={key}>
                {label}
                <input
                  required
                  pattern={
                    mode === "register" && key === "userName"
                      ? "[A-Za-z0-9._-]{3,50}"
                      : mode === "register" && key === "phone"
                        ? "[+0-9 .-]{8,15}"
                        : undefined
                  }
                  title={
                    mode === "register" && key === "userName"
                      ? "Từ 3 đến 50 ký tự: chữ không dấu, số, dấu chấm, gạch dưới hoặc gạch ngang"
                      : undefined
                  }
                  type={type}
                  autoComplete={
                    type === "password"
                      ? mode === "login"
                        ? "current-password"
                        : "new-password"
                      : key === "userName"
                        ? "username"
                        : key === "email"
                          ? "email"
                          : "on"
                  }
                  minLength={
                    type === "password" && mode !== "login" ? 8 : undefined
                  }
                  maxLength={type === "password" ? 72 : 255}
                  value={form[key] || ""}
                  onChange={(e) => setForm({ ...form, [key]: e.target.value })}
                />
              </label>
            ))}
            {mode === "login" && (
              <Link className="forgot" to="/forgot-password">
                Quên mật khẩu?
              </Link>
            )}
            {error && (
              <p className="error" role="alert">
                {error}
              </p>
            )}
            <button className="button full" disabled={busy}>
              {busy
                ? "Đang xử lý…"
                : mode === "login"
                  ? "Đăng nhập"
                  : mode === "register"
                    ? "Tạo tài khoản"
                    : mode === "forgot"
                      ? "Gửi liên kết đặt lại"
                      : "Lưu mật khẩu mới"}
            </button>
          </form>
        )}
        <p className="muted">
          {mode === "login" ? (
            <>
              Chưa có tài khoản? <Link to="/register">Đăng ký</Link>
            </>
          ) : (
            <Link to="/login">Quay lại đăng nhập</Link>
          )}
        </p>
      </section>
    </div>
  );
}
