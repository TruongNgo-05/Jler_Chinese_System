import { useData } from "@/shared/hooks/useData";
import { orderApi } from "@/features/orders/api/orderApi";
import { useSearchParams } from "react-router-dom";
import { useApp } from "@/shared/context/AppContext";
import { useState } from "react";
import { message } from "@/shared/utils/errors";

export function useOrdersController() {
  const d = useData(orderApi.path);
  const [params, setParams] = useSearchParams();
  const selected = params.get("pay");
  const { notify } = useApp();
  const [busy, setBusy] = useState(null);
  async function cancel(id) {
    if (!window.confirm("Hủy đơn hàng này?")) return;
    setBusy(id);
    try {
      await orderApi.action(id, { action: "CANCEL" });
      d.reload();
      notify("Đã hủy đơn hàng");
    } catch (e) {
      notify(message(e), true);
    } finally {
      setBusy(null);
    }
  }
  return { d, setParams, busy, cancel, selected };
}
