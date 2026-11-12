export const message = (e) =>
  e.response?.data?.message ||
  (e.code === "ECONNABORTED"
    ? "Máy chủ phản hồi chậm. Vui lòng thử lại."
    : "Không kết nối được máy chủ. Vui lòng thử lại.");
