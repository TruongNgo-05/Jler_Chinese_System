import { useParams } from "react-router-dom";
import { useData } from "@/shared/hooks/useData";
import { catalogApi } from "@/features/catalog/api/catalogApi";
import { useApp } from "@/shared/context/AppContext";
import { useState } from "react";
import { saveSelection } from "@/features/catalog/api/productActions";
import { cartChanged } from "@/shared/events/cartEvents";
import { message } from "@/shared/utils/errors";

export function useDetailController({ kind }) {
  const { id } = useParams();
  const d = useData(catalogApi.detailPath(kind, id));
  const { user, notify } = useApp();
  const [qty, setQty] = useState(1);
  const [selectedImage, setSelectedImage] = useState(null);
  const [busy, setBusy] = useState(false);
  async function action(favorite = false) {
    if (!user) {
      notify("Vui lòng đăng nhập trước.");
      return;
    }
    setBusy(true);
    try {
      await saveSelection(favorite, {
        itemId: Number(id),
        itemType: kind === "books" ? "Book" : "Course",
        quantity: qty,
      });
      if (!favorite) cartChanged();
      notify(favorite ? "Đã cập nhật yêu thích" : "Đã thêm vào giỏ hàng");
    } catch (e) {
      notify(message(e), true);
    } finally {
      setBusy(false);
    }
  }
  const p = d.data;
  return {
    d,
    p,
    selectedImage,
    id,
    qty,
    setQty,
    busy,
    action,
    setSelectedImage,
  };
}
