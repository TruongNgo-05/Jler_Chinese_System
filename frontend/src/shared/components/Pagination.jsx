export function Pager({ page, setPage, total, size = 9 }) {
  const pages = Math.ceil(total / size);
  return (
    pages > 1 && (
      <nav className="pagination" aria-label="Phân trang">
        <button disabled={page <= 1} onClick={() => setPage(page - 1)}>
          Trước
        </button>
        <span>
          {page} / {pages}
        </span>
        <button disabled={page >= pages} onClick={() => setPage(page + 1)}>
          Tiếp
        </button>
      </nav>
    )
  );
}
