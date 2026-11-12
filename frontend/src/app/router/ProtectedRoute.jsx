import { useApp } from "@/shared/context/AppContext";
import { Navigate, Link, Outlet } from "react-router-dom";

export function Protected({ admin = false }) {
  const { user, loading } = useApp();
  if (loading)
    return <div className="state">Đang kiểm tra phiên đăng nhập…</div>;
  if (!user) return <Navigate to="/login" replace />;
  if (admin && user.role !== "ADMIN")
    return (
      <div className="state error">
        Bạn không có quyền truy cập trang quản trị.{" "}
        <Link to="/">Về trang chủ</Link>
      </div>
    );
  return <Outlet />;
}
