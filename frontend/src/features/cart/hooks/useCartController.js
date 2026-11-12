import { useData } from "@/shared/hooks/useData";
import { cartApi } from "@/features/cart/api/cartApi";
import { useApp } from "@/shared/context/AppContext";
import { useState, useRef } from "react";
import { cartChanged } from "@/shared/events/cartEvents";
import { message } from "@/shared/utils/errors";

export function useCartController() {
  const d = useData(cartApi.path);
  const { notify } = useApp();
  const [busy, setBusy] = useState(false);
  const pending = useRef(false);
  async function update(id, delta) {
    if (pending.current) return;
    pending.current = true;
    setBusy(true);
    try {
      const r =
        delta === 0
          ? await cartApi.remove(id)
          : await cartApi.adjust(id, { delta });
      if (r.data?.items) d.setData(r.data);
      else await d.reload();
      cartChanged();
    } catch (e) {
      notify(message(e), true);
    } finally {
      pending.current = false;
      setBusy(false);
    }
  }
  return { d, busy, update };
}
