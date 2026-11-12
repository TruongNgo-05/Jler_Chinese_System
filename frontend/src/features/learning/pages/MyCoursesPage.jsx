import { useData } from "@/shared/hooks/useData";
import { learningApi } from "@/features/learning/api/learningApi";
import { Heading } from "@/shared/components/PageHeading";
import { State } from "@/shared/components/AsyncState";
import { Link } from "react-router-dom";
import { Image } from "@/shared/components/MediaImage";
import { Empty } from "@/shared/components/EmptyState";

export function MyCourses() {
  const d = useData(learningApi.myCoursesPath);
  return (
    <section className="section">
      <Heading title="Khóa học của tôi" eyebrow="TIẾP NỐI MỖI NGÀY" />
      <State {...d} retry={d.reload} />
      <div className="product-grid">
        {d.data?.map((c) => (
          <Link
            className="product-card learning-card"
            key={c.id}
            to={`/learn/${c.id}`}
          >
            <Image src={c.imageUrl} alt={c.title} />
            <div className="product-body">
              <p className="eyebrow">{c.courseType}</p>
              <h2>{c.title}</h2>
              <span className="button small">Vào học →</span>
            </div>
          </Link>
        ))}
      </div>
      {d.data?.length === 0 && (
        <Empty>
          Bạn chưa có khóa học. <Link to="/courses">Khám phá khóa học →</Link>
        </Empty>
      )}
    </section>
  );
}
