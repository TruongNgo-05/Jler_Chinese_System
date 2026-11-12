import { useData } from "@/shared/hooks/useData";
import { favoritesApi } from "@/features/favorites/api/favoritesApi";
import { useApp } from "@/shared/context/AppContext";
import { message } from "@/shared/utils/errors";
import { Heading } from "@/shared/components/PageHeading";
import { State } from "@/shared/components/AsyncState";
import { ProductCard } from "@/features/catalog/components/ProductCard";
import { Empty } from "@/shared/components/EmptyState";

export function Favorites() {
  const d = useData(favoritesApi.path);
  const { notify } = useApp();
  async function remove(f) {
    try {
      await favoritesApi.toggle(f);
      d.reload();
    } catch (e) {
      notify(message(e), true);
    }
  }
  return (
    <section className="section">
      <Heading title="Danh sách yêu thích" eyebrow="ĐỂ DÀNH CHO NGÀY MAI" />
      <State {...d} retry={d.reload} />
      <div className="product-grid">
        {d.data?.map((f) => (
          <div key={f.id}>
            <ProductCard
              item={f.product}
              kind={f.itemType === "Book" ? "books" : "courses"}
            />
            <button
              className="link-button"
              onClick={() => remove({ itemId: f.itemId, itemType: f.itemType })}
            >
              Bỏ lưu
            </button>
          </div>
        ))}
      </div>
      {d.data?.length === 0 && <Empty>Chưa có mục yêu thích.</Empty>}
    </section>
  );
}
