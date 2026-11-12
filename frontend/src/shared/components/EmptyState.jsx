export function Empty({ children = "Chưa có dữ liệu." }) {
  return (
    <div className="empty">
      <span>◌</span>
      <p>{children}</p>
    </div>
  );
}
