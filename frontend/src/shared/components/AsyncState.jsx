export function State({ loading, error, retry }) {
  return loading ? (
    <div className="state">
      <span className="spinner" />
      Đang tải dữ liệu…
    </div>
  ) : error ? (
    <div className="state error" role="alert">
      {error}
      {retry && <button onClick={retry}>Thử lại</button>}
    </div>
  ) : null;
}
