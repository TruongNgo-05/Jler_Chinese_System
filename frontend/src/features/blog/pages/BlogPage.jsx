import { useData } from "@/shared/hooks/useData";
import { catalogApi } from "@/features/catalog/api/catalogApi";
import { Heading } from "@/shared/components/PageHeading";
import { State } from "@/shared/components/AsyncState";
import { Link } from "react-router-dom";
import { Image } from "@/shared/components/MediaImage";
import { Empty } from "@/shared/components/EmptyState";

export function Blog() {
  const d = useData(catalogApi.listPath("posts"));
  return (
    <section className="section">
      <Heading eyebrow="CHUYỆN HỌC TIẾNG TRUNG" title="Góc học tập" />
      <State {...d} retry={d.reload} />
      <div className="blog-grid">
        {d.data
          ?.filter((p) => p.type === "BLOG")
          .map((p) => (
            <Link className="blog-card" key={p.id} to={`/blog/${p.id}`}>
              <Image src={p.imageUrl} alt={p.title} />
              <div>
                <p className="eyebrow">JLer JOURNAL</p>
                <h2>{p.title}</h2>
                <p>{p.content.slice(0, 140)}…</p>
                <span className="text-link">Đọc tiếp →</span>
              </div>
            </Link>
          ))}
      </div>
      {d.data && !d.data.some((p) => p.type === "BLOG") && (
        <Empty>Chưa có bài viết.</Empty>
      )}
    </section>
  );
}
