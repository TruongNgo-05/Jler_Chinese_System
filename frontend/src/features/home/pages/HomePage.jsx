import { useData } from "@/shared/hooks/useData";
import { catalogApi } from "@/features/catalog/api/catalogApi";
import { useState } from "react";
import { Link } from "react-router-dom";
import { Image } from "@/shared/components/MediaImage";
import { Heading } from "@/shared/components/PageHeading";
import { State } from "@/shared/components/AsyncState";
import { ProductCard } from "@/features/catalog/components/ProductCard";
import { Empty } from "@/shared/components/EmptyState";

export function Home() {
  const books = useData(catalogApi.listPath("books"));
  const courses = useData(catalogApi.listPath("courses"));
  const posts = useData(catalogApi.listPath("posts"));
  const [bannerIndex, setBannerIndex] = useState(0);
  const banners = posts.data?.filter((p) => p.type === "BANNER") || [];
  const banner = banners[bannerIndex % Math.max(1, banners.length)];
  return (
    <>
      <section className="hero">
        <div className="hero-copy">
          <span className="pill">你好 · Xin chào, người bạn mới</span>
          <h1>
            {banner?.title || (
              <>
                Mở một trang sách.
                <br />
                Mở cả <em>tương lai.</em>
              </>
            )}
          </h1>
          <p>
            {banner?.content ||
              "Từ những lời chào đầu tiên đến hành trình chinh phục HSK. Khám phá tiếng Trung cùng JLer, theo nhịp của riêng bạn."}
          </p>
          <div className="hero-buttons">
            <Link className="button" to="/courses">
              Khám phá khóa học ↗
            </Link>
            <Link className="text-link" to="/books">
              Ghé hiệu sách →
            </Link>
          </div>
          {banners.length > 1 && (
            <div className="banner-controls">
              <button
                aria-label="Banner trước"
                onClick={() =>
                  setBannerIndex(
                    (bannerIndex + banners.length - 1) % banners.length,
                  )
                }
              >
                ←
              </button>
              <span>
                {(bannerIndex % banners.length) + 1} / {banners.length}
              </span>
              <button
                aria-label="Banner tiếp theo"
                onClick={() =>
                  setBannerIndex((bannerIndex + 1) % banners.length)
                }
              >
                →
              </button>
            </div>
          )}
          <div className="hero-note">
            <span>01</span>
            <p>
              Mỗi ngày một chút.
              <br />
              <strong>Đi xa hơn bạn nghĩ.</strong>
            </p>
          </div>
        </div>
        <div className="hero-art">
          <div className="circle-text">HỌC · KHÁM PHÁ · KẾT NỐI</div>
          {banner?.imageUrl ? (
            <Image
              className="hero-custom-image"
              src={banner.imageUrl}
              alt={banner.title}
            />
          ) : (
            <div className="hanzi">
              学<span>中文</span>
            </div>
          )}
          <div className="art-card">
            <span>你的未来</span>
            <strong>Tương lai của bạn</strong>
            <small>Bắt đầu từ hôm nay</small>
          </div>
          <div className="art-seal">
            JLer
            <br />
            你好
          </div>
        </div>
      </section>
      <div className="benefits">
        <div>
          <b>01 / LỘ TRÌNH RÕ RÀNG</b>
          <p>Học từng bước, chắc nền tảng</p>
        </div>
        <div>
          <b>02 / HỌC & THỰC HÀNH</b>
          <p>Bài học, flashcard và luyện thi</p>
        </div>
        <div>
          <b>03 / KHÔNG GIAN CỦA BẠN</b>
          <p>Lưu tiến độ, tiếp nối mỗi ngày</p>
        </div>
      </div>
      <section className="section">
        <Heading
          eyebrow="CHỌN ĐIỂM KHỞI ĐẦU"
          title="Hành trình tiếng Trung của bạn"
        >
          <Link className="text-link" to="/courses">
            Tất cả khóa học →
          </Link>
        </Heading>
        <State {...courses} retry={courses.reload} />
        <div className="product-grid">
          {courses.data?.slice(0, 3).map((c) => (
            <ProductCard key={c.id} item={c} kind="courses" />
          ))}
        </div>
        {courses.data?.length === 0 && (
          <Empty>Khóa học đang được cập nhật.</Empty>
        )}
      </section>
      <section className="section paper">
        <Heading
          eyebrow="MỘT CUỐN SÁCH, NHIỀU KHỞI ĐẦU"
          title="Ghé thăm hiệu sách JLer"
        >
          <Link className="text-link" to="/books">
            Xem tất cả →
          </Link>
        </Heading>
        <State {...books} retry={books.reload} />
        <div className="product-grid">
          {books.data?.slice(0, 3).map((b) => (
            <ProductCard key={b.id} item={b} />
          ))}
        </div>
        {books.data?.length === 0 && <Empty>Sách đang được cập nhật.</Empty>}
      </section>
      {posts.data
        ?.filter((p) => p.type === "NOTICE")
        .map((p) => (
          <section className="notice section" key={p.id}>
            <Image src={p.imageUrl} alt={p.title} />
            <div>
              <p className="eyebrow">THÔNG BÁO JLer</p>
              <h2>{p.title}</h2>
              <p className="pre-line">{p.content}</p>
            </div>
          </section>
        ))}
    </>
  );
}
