import { useData } from "@/shared/hooks/useData";
import { catalogApi } from "@/features/catalog/api/catalogApi";
import { useState } from "react";

export function useCatalogController({ kind }) {
  const data = useData(catalogApi.listPath(kind));
  const categories = useData(catalogApi.listPath("categories"));
  const [query, setQuery] = useState("");
  const [category, setCategory] = useState("");
  const [sort, setSort] = useState("");
  const [page, setPage] = useState(1);
  const [min, setMin] = useState("");
  const [max, setMax] = useState("");
  const filtered = (data.data || [])
    .filter(
      (x) =>
        x.title
          .toLocaleLowerCase("vi")
          .includes(query.toLocaleLowerCase("vi")) &&
        (!category ||
          (kind === "books"
            ? x.categoryIds?.includes(Number(category))
            : x.courseType === category)) &&
        (min === "" || Number(x.discountPrice ?? x.price) >= Number(min)) &&
        (max === "" || Number(x.discountPrice ?? x.price) <= Number(max)),
    )
    .sort((a, b) =>
      sort === "asc"
        ? (a.discountPrice ?? a.price) - (b.discountPrice ?? b.price)
        : sort === "desc"
          ? (b.discountPrice ?? b.price) - (a.discountPrice ?? a.price)
          : b.id - a.id,
    );
  return {
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
  };
}
