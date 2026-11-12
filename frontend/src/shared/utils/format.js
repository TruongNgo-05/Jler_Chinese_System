export const money = (n) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    Number(n || 0),
  );
export const date = (d) => (d ? new Date(d).toLocaleString("vi-VN") : "");
