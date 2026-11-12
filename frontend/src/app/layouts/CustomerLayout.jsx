import { useApp } from "@/shared/context/AppContext";
import { useState, useEffect } from "react";
import { useLocation, NavLink, Link, Outlet } from "react-router-dom";
import { useData } from "@/shared/hooks/useData";
import { cartApi } from "@/features/cart/api/cartApi";
import { Brand } from "@/shared/components/Brand";

export function Layout() {
  const { user, logout } = useApp();
  const [menu, setMenu] = useState(false);
  const location = useLocation();
  const cart = useData(user ? cartApi.path : null);
  const reloadCart = cart.reload;
  useEffect(() => {
    window.addEventListener("cart-updated", reloadCart);
    window.addEventListener("focus", reloadCart);
    return () => {
      window.removeEventListener("cart-updated", reloadCart);
      window.removeEventListener("focus", reloadCart);
    };
  }, [reloadCart]);
  useEffect(() => {
    window.scrollTo({ top: 0 });
  }, [location.pathname]);
  return (
    <>
      <div className="topline">JLer · Mỗi ngày một bước, chạm tới ước mơ</div>
      <header className="site-header">
        <Brand />
        <button
          className="menu-toggle"
          aria-label="Mở menu"
          aria-expanded={menu}
          onClick={() => setMenu(!menu)}
        >
          ☰
        </button>
        <nav
          className={menu ? "main-nav open" : "main-nav"}
          onClick={() => setMenu(false)}
        >
          <NavLink end to="/">
            Trang chủ
          </NavLink>
          <NavLink to="/courses">Khóa học</NavLink>
          <NavLink to="/books">Sách & giáo trình</NavLink>
          <NavLink to="/practice">Luyện thi</NavLink>
          <NavLink to="/blog">Góc học tập</NavLink>
        </nav>
        <div className="header-actions">
          <Link to="/cart" className="cart-link">
            Giỏ hàng
            {user && cart.data?.items?.length > 0 && (
              <span className="cart-badge">
                {cart.data.totalItems ??
                  cart.data.items.reduce((n, i) => n + i.quantity, 0)}
              </span>
            )}
          </Link>
          {user ? (
            <details className="account">
              <summary>{user.fullName || user.userName} ▾</summary>
              <div>
                <Link to="/my-courses">Khóa học của tôi</Link>
                <Link to="/vocabulary">Sổ từ vựng</Link>
                <Link to="/orders">Đơn hàng</Link>
                <Link to="/favorites">Yêu thích</Link>
                <Link to="/profile">Tài khoản</Link>
                {user.role === "ADMIN" && <Link to="/admin">Quản trị</Link>}
                <button onClick={logout}>Đăng xuất</button>
              </div>
            </details>
          ) : (
            <Link className="button small" to="/login">
              Đăng nhập
            </Link>
          )}
        </div>
      </header>
      <main>
        <Outlet />
      </main>
      <footer>
        <div className="footer-grid">
          <div>
            <strong className="footer-brand">
              JLer<span>学</span>
            </strong>
            <p>Tiếng Trung mở lối tương lai.</p>
            <p>Cùng bạn học, luyện tập và tiến bộ mỗi ngày.</p>
          </div>
          <div>
            <h3>Khám phá</h3>
            <Link to="/courses">Khóa học tiếng Trung</Link>
            <Link to="/books">Sách & giáo trình</Link>
            <Link to="/practice">Luyện tập trắc nghiệm</Link>
          </div>
          <div>
            <h3>Góc của bạn</h3>
            <Link to="/my-courses">Tiếp tục học</Link>
            <Link to="/orders">Theo dõi đơn hàng</Link>
            <Link to="/profile">Thông tin cá nhân</Link>
          </div>
        </div>
        <div className="footer-bottom">
          © {new Date().getFullYear()} JLer · Học theo cách của bạn.
          <a
            href="#"
            onClick={(e) => {
              e.preventDefault();
              window.scrollTo({ top: 0, behavior: "smooth" });
            }}
          >
            Về đầu trang ↑
          </a>
        </div>
      </footer>
    </>
  );
}
