import { useData } from "@/shared/hooks/useData";
import { cartApi } from "@/features/cart/api/cartApi";
import { useApp } from "@/shared/context/AppContext";
import { useState, useRef } from "react";
import { paymentApi } from "@/features/payments/api/paymentApi";
import { useNavigate } from "react-router-dom";
import { message } from "@/shared/utils/errors";
import { orderApi } from "@/features/orders/api/orderApi";
import { cartChanged } from "@/shared/events/cartEvents";

export function useCheckoutController() {
  const d = useData(cartApi.path);
  const { user, notify } = useApp();
  const [form, setForm] = useState({
    fullName: user.fullName || "",
    phone: user.phone || "",
    address: user.address || "",
    paymentMethod: "SEPAY",
    voucherCode: "",
    note: "",
  });
  const options = useData(paymentApi.optionsPath);
  const [quote, setQuote] = useState(null);
  const [appliedCode, setAppliedCode] = useState("");
  const submitting = useRef(false);
  const [busy, setBusy] = useState(false);
  const nav = useNavigate();
  const physical = d.data?.items.some((i) => i.itemType === "Book");
  const sub = Number(d.data?.subtotal || 0);
  const totals = quote || {
    subtotal: sub,
    discount: 0,
    shippingFee: physical ? 30000 : 0,
    total: sub + (physical ? 30000 : 0),
  };
  const paymentMethod =
    totals.total === 0
      ? "FREE"
      : form.paymentMethod === "SEPAY" &&
          !options.data?.sepayEnabled &&
          physical
        ? "COD"
        : form.paymentMethod;
  function field(key, value) {
    setForm({ ...form, [key]: value });
    if (key === "voucherCode") {
      setQuote(null);
      setAppliedCode("");
    }
  }
  async function apply() {
    setBusy(true);
    try {
      const r = await cartApi.quote(form.voucherCode);
      setQuote(r.data);
      setAppliedCode(form.voucherCode.trim());
      notify("Đã áp dụng mã giảm giá");
    } catch (e) {
      setQuote(null);
      setAppliedCode("");
      notify(message(e), true);
    } finally {
      setBusy(false);
    }
  }
  async function submit(e) {
    e.preventDefault();
    if (submitting.current) return;
    submitting.current = true;
    setBusy(true);
    try {
      const payload = { ...form, voucherCode: appliedCode, paymentMethod };
      const storageKey = `jler-checkout-${user.id ?? user.userId ?? user.userName}`;
      const fingerprint = JSON.stringify(payload);
      let saved;
      try {
        saved = JSON.parse(sessionStorage.getItem(storageKey));
      } catch {
        /* discard invalid cache */
      }
      const checkoutKey =
        saved?.fingerprint === fingerprint ? saved.key : crypto.randomUUID();
      sessionStorage.setItem(
        storageKey,
        JSON.stringify({ fingerprint, key: checkoutKey }),
      );
      const r = await orderApi.create({ ...payload, checkoutKey });
      sessionStorage.removeItem(storageKey);
      cartChanged();
      notify(
        paymentMethod === "SEPAY"
          ? "Đã tạo đơn. Quét QR để thanh toán."
          : "Đặt hàng thành công.",
      );
      nav(paymentMethod === "SEPAY" ? `/orders?pay=${r.data.id}` : "/orders");
    } catch (e) {
      notify(message(e), true);
    } finally {
      submitting.current = false;
      setBusy(false);
    }
  }
  return {
    d,
    submit,
    physical,
    form,
    field,
    options,
    paymentMethod,
    totals,
    busy,
    apply,
  };
}
