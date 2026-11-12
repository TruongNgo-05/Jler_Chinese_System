import { useCatalogController } from "@/features/catalog/hooks/useCatalogController";
import { Heading } from "@/shared/components/PageHeading";
import { State } from "@/shared/components/AsyncState";
import { ProductCard } from "@/features/catalog/components/ProductCard";
import { Empty } from "@/shared/components/EmptyState";
import { Pager } from "@/shared/components/Pagination";

export function Catalog({ kind }) {
  const {
    query,
    setQuery,
    setPage,
    category,
    setCategory,
    categories,
    data,
    min,
    setMin,
    max,
    setMax,
    sort,
    setSort,
    filtered,
    page,
  } = useCatalogController({ kind });
  return (
    <section className="section">
      <Heading
        eyebrow="HỌC THEO CÁCH CỦA BẠN"
        title={kind === "books" ? "Sách & giáo trình" : "Khóa học tiếng Trung"}
      />
      <div className="filters">
        <input
          aria-label="Tìm kiếm"
          placeholder="Bạn muốn tìm gì?"
          value={query}
          onChange={(e) => {
            setQuery(e.target.value);
            setPage(1);
          }}
        />
        <select
          aria-label="Danh mục"
          value={category}
          onChange={(e) => {
            setCategory(e.target.value);
            setPage(1);
          }}
        >
          <option value="">Tất cả danh mục</option>
          {kind === "books"
            ? categories.data?.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))
            : [...new Set(data.data?.map((c) => c.courseType))]
                .filter(Boolean)
                .map((c) => <option key={c}>{c}</option>)}
        </select>
        <input
          type="number"
          min="0"
          aria-label="Giá từ"
          placeholder="Giá từ"
          value={min}
          onChange={(e) => {
            setMin(e.target.value);
            setPage(1);
          }}
        />
        <input
          type="number"
          min="0"
          aria-label="Giá đến"
          placeholder="Giá đến"
          value={max}
          onChange={(e) => {
            setMax(e.target.value);
            setPage(1);
          }}
        />
        <select
          aria-label="Sắp xếp"
          value={sort}
          onChange={(e) => setSort(e.target.value)}
        >
          <option value="">Mới nhất</option>
          <option value="asc">Giá tăng dần</option>
          <option value="desc">Giá giảm dần</option>
        </select>
      </div>
      <State {...data} retry={data.reload} />
      {data.data && <p className="muted">{filtered.length} kết quả</p>}
      <div className="product-grid">
        {filtered.slice((page - 1) * 9, page * 9).map((x) => (
          <ProductCard key={x.id} item={x} kind={kind} />
        ))}
      </div>
      {data.data && filtered.length === 0 && (
        <Empty>Không tìm thấy kết quả phù hợp.</Empty>
      )}
      <Pager page={page} setPage={setPage} total={filtered.length} />
    </section>
  );
}
