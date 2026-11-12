import { useApp } from "@/shared/context/AppContext";
import { useParams, NavLink, Link, Outlet } from "react-router-dom";
import { useState } from "react";
import { Brand } from "@/shared/components/Brand";
import { adminMenu } from "@/features/admin/config/menu";

export function AdminLayout() {
  const { user, logout } = useApp();
  const { kind = "dashboard" } = useParams();
  const [open, setOpen] = useState(false);
  return (
    <div className={`admin-shell ${open ? "menu-open" : ""}`}>
      <aside className="console-sidebar">
        <div className="console-brand">
          <Brand to="/admin/dashboard" />
          <span>QUẢN TRỊ</span>
        </div>
        <nav aria-label="Điều hướng quản trị" onClick={() => setOpen(false)}>
          {adminMenu.map(([key, label]) => (
            <NavLink key={key} end to={`/admin/${key}`}>
              <span className="nav-dot" />
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="console-sidebar-bottom">JLer · Learning & Books</div>
      </aside>
      {open && (
        <button
          className="console-overlay"
          aria-label="Đóng menu quản trị"
          onClick={() => setOpen(false)}
        />
      )}
      <div className="console-workspace">
        <header className="admin-header">
          <div className="console-title">
            <button
              className="console-menu-button"
              aria-label="Mở menu quản trị"
              aria-expanded={open}
              onClick={() => setOpen(!open)}
            >
              ☰
            </button>
            <div>
              <small>JLer / QUẢN TRỊ</small>
              <strong>
                {adminMenu.find((x) => x[0] === kind)?.[1] || "Quản lý"}
              </strong>
            </div>
          </div>
          <div className="admin-header-actions">
            <Link to="/" className="console-site-link">
              Xem website ↗
            </Link>
            <div className="console-user">
              <span>{user.fullName || user.userName}</span>
              <small>Quản trị viên</small>
            </div>
            <button onClick={logout}>Đăng xuất</button>
          </div>
        </header>
        <main className="console-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
