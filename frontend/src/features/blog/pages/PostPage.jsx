import { useParams, Link } from "react-router-dom";
import { useData } from "@/shared/hooks/useData";
import { catalogApi } from "@/features/catalog/api/catalogApi";
import { State } from "@/shared/components/AsyncState";
import { Image } from "@/shared/components/MediaImage";

export function Post() {
  const { id } = useParams();
  const d = useData(catalogApi.detailPath("posts", id));
  return (
    <article className="section article">
      <Link to="/blog">← Góc học tập</Link>
      <State {...d} retry={d.reload} />
      {d.data && (
        <>
          <h1>{d.data.title}</h1>
          <Image src={d.data.imageUrl} alt={d.data.title} />
          <p className="pre-line">{d.data.content}</p>
        </>
      )}
    </article>
  );
}
