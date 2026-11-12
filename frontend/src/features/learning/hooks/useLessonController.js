import { useApp } from "@/shared/context/AppContext";
import { useState } from "react";
import { useData } from "@/shared/hooks/useData";
import { learningApi } from "@/features/learning/api/learningApi";
import { message } from "@/shared/utils/errors";

export function useLessonController({ lesson, onComplete }) {
  const { notify, user } = useApp();
  const [tab, setTab] = useState("content");
  const [index, setIndex] = useState(0);
  const [flipped, setFlipped] = useState(false);
  const [comment, setComment] = useState("");
  const [busy, setBusy] = useState(false);
  const cards = useData(learningApi.cardsPath(lesson.id));
  const comments = useData(learningApi.commentsPath(lesson.id));
  async function progress() {
    setBusy(true);
    try {
      await learningApi.progress(lesson.id, {
        completed: true,
      });
      onComplete();
      notify("Đã lưu tiến độ học");
    } catch (e) {
      notify(message(e), true);
    } finally {
      setBusy(false);
    }
  }
  async function send(e) {
    e.preventDefault();
    setBusy(true);
    try {
      await learningApi.comment(lesson.id, {
        content: comment,
      });
      setComment("");
      comments.reload();
    } catch (e) {
      notify(message(e), true);
    } finally {
      setBusy(false);
    }
  }
  async function remove(id) {
    try {
      await learningApi.deleteComment(id);
      comments.reload();
    } catch (e) {
      notify(message(e), true);
    }
  }
  const card = cards.data?.[index];
  return {
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
  };
}
