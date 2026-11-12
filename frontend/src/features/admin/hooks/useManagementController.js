import { useData } from "@/shared/hooks/useData";
import { adminApi } from "@/features/admin/api/adminApi";
import { useState } from "react";
import { useApp } from "@/shared/context/AppContext";
import { message } from "@/shared/utils/errors";
import { adminMenu as menus } from "@/features/admin/config/menu";

export function useManagementController({ kind }) {
  const d = useData(adminApi.listPath(kind));
  const [query, setQuery] = useState("");
  const [page, setPage] = useState(1);
  const [editing, setEditing] = useState(null);
  const { notify } = useApp();
  const [busy, setBusy] = useState(null);
  const filtered = (d.data || []).filter((x) =>
    JSON.stringify(x).toLowerCase().includes(query.toLowerCase()),
  );
  async function remove(row) {
    if (
      !window.confirm("Xóa mục này? Dữ liệu đang được sử dụng sẽ được bảo vệ.")
    )
      return;
    setBusy(row.id);
    try {
      await adminApi.remove(kind, row.id);
      d.reload();
      notify("Đã xóa");
    } catch (e) {
      notify(message(e), true);
    } finally {
      setBusy(null);
    }
  }
  const title = menus.find((x) => x[0] === kind)?.[1];
  return {
    title,
    setEditing,
    query,
    setQuery,
    setPage,
    d,
    filtered,
    page,
    busy,
    setBusy,
    notify,
    remove,
    editing,
  };
}
