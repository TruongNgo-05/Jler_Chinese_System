import { useLessonController } from "@/features/learning/hooks/useLessonController";
import { media } from "@/shared/utils/media";
import { learningApi } from "@/features/learning/api/learningApi";
import { State } from "@/shared/components/AsyncState";
import { Image } from "@/shared/components/MediaImage";
import { Empty } from "@/shared/components/EmptyState";
import { date } from "@/shared/utils/format";

export function Lesson({ lesson, completed, onComplete, position = 0 }) {
  const {
    busy,
    progress,
    tab,
    setTab,
    cards,
    card,
    flipped,
    setFlipped,
    index,
    setIndex,
    send,
    comment,
    setComment,
    comments,
    user,
    remove,
  } = useLessonController({ lesson, completed, onComplete, position });
  return (
    <div className="lesson-panel">
      <div className="heading">
        <h2>{lesson.title}</h2>
        <button
          className="button small"
          disabled={busy || completed}
          onClick={progress}
        >
          {completed ? "✓ Đã hoàn thành" : "Đánh dấu hoàn thành"}
        </button>
      </div>
      <div className="tabs">
        {[
          ["content", "Bài học"],
          ["cards", "Flashcard"],
          ["comments", "Thảo luận"],
        ].map(([key, label]) => (
          <button
            key={key}
            className={tab === key ? "active" : ""}
            onClick={() => setTab(key)}
          >
            {label}
          </button>
        ))}
      </div>
      {tab === "content" && (
        <>
          {lesson.videoUrl && (
            <video
              controls
              preload="metadata"
              src={media(lesson.videoUrl)}
              onLoadedMetadata={(e) => {
                if (position > 0 && position < e.currentTarget.duration)
                  e.currentTarget.currentTime = position;
              }}
              onTimeUpdate={(e) => {
                const pos = Math.floor(e.currentTarget.currentTime);
                if (
                  pos > 0 &&
                  pos % 30 === 0 &&
                  e.currentTarget.dataset.saved !== String(pos)
                ) {
                  e.currentTarget.dataset.saved = String(pos);
                  learningApi
                    .progress(lesson.id, {
                      lastPosition: pos,
                    })
                    .catch(() => {});
                }
              }}
            />
          )}
          <div className="lesson-text pre-line">
            {lesson.content || "Nội dung bài học đang được cập nhật."}
          </div>
          {lesson.attachmentUrl && (
            <a
              className="button secondary"
              href={media(lesson.attachmentUrl)}
              target="_blank"
              rel="noreferrer"
            >
              Mở tài liệu ↗
            </a>
          )}
        </>
      )}
      {tab === "cards" && (
        <>
          <State {...cards} />
          {card ? (
            <>
              <button
                className={`flashcard ${flipped ? "flipped" : ""}`}
                onClick={() => setFlipped(!flipped)}
              >
                <small>
                  {flipped ? "NGHĨA TIẾNG VIỆT" : "TỪ VỰNG · BẤM ĐỂ LẬT"}
                </small>
                <strong>{flipped ? card.back : card.front}</strong>
                {card.imageUrl && (
                  <Image src={card.imageUrl} alt="Minh họa từ vựng" />
                )}
              </button>
              <div className="pagination">
                <button
                  disabled={index === 0}
                  onClick={() => {
                    setIndex(index - 1);
                    setFlipped(false);
                  }}
                >
                  Trước
                </button>
                <span>
                  {index + 1} / {cards.data.length}
                </span>
                <button
                  disabled={index === cards.data.length - 1}
                  onClick={() => {
                    setIndex(index + 1);
                    setFlipped(false);
                  }}
                >
                  Tiếp
                </button>
              </div>
            </>
          ) : (
            cards.data && <Empty>Chưa có flashcard.</Empty>
          )}
        </>
      )}
      {tab === "comments" && (
        <>
          <form className="inline" onSubmit={send}>
            <input
              aria-label="Bình luận"
              placeholder="Chia sẻ câu hỏi của bạn…"
              required
              maxLength="1000"
              value={comment}
              onChange={(e) => setComment(e.target.value)}
            />
            <button className="button" disabled={busy}>
              Gửi
            </button>
          </form>
          <State {...comments} />
          {comments.data?.map((c) => (
            <article className="comment" key={c.id}>
              <b>{c.user}</b>
              <small>{date(c.createdAt)}</small>
              <p>{c.content}</p>
              {(user.id === c.userId || user.role === "ADMIN") && (
                <button className="link-button" onClick={() => remove(c.id)}>
                  Xóa
                </button>
              )}
            </article>
          ))}
          {comments.data?.length === 0 && (
            <Empty>Hãy bắt đầu cuộc thảo luận.</Empty>
          )}
        </>
      )}
    </div>
  );
}
