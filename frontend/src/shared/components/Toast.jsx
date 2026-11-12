export function Toast({ toast, close }) {
  return toast ? (
    <div role="status" className={`toast ${toast.error ? "error" : ""}`}>
      {toast.text}
      <button aria-label="Đóng thông báo" onClick={close}>
        ×
      </button>
    </div>
  ) : null;
}
