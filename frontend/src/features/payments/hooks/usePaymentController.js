import { useState, useEffectEvent, useEffect } from "react";
import { paymentApi } from "@/features/payments/api/paymentApi";
import { message } from "@/shared/utils/errors";

export function usePaymentController({ orderId, onPaid }) {
  const [data, setData] = useState(null);
  const [error, setError] = useState("");
  const [version, setVersion] = useState(0);
  const [qrError, setQrError] = useState(false);
  const paid = useEffectEvent(() => onPaid?.());
  useEffect(() => {
    let active = true,
      timer;
    let announced = false;
    async function refresh() {
      try {
        const r = await paymentApi.get(orderId);
        if (!active) return;
        setData(r.data);
        setError("");
        if (r.data.paymentStatus === "paid") {
          if (!announced) {
            announced = true;
            paid();
          }
        } else if (r.data.status !== "CANCELED" && r.data.method === "SEPAY") {
          timer = setTimeout(refresh, 5000);
        }
      } catch (e) {
        if (!active) return;
        setError(message(e));
        timer = setTimeout(refresh, 10000);
      }
    }
    refresh();
    return () => {
      active = false;
      clearTimeout(timer);
    };
  }, [orderId, version]);
  return { data, error, setVersion, qrError, setQrError };
}
