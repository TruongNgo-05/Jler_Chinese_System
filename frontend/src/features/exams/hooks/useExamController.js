import { useParams, useNavigate } from "react-router-dom";
import { useApp } from "@/shared/context/AppContext";
import { useState, useRef, useEffectEvent, useEffect } from "react";
import { examApi } from "@/features/exams/api/examApi";
import { message } from "@/shared/utils/errors";

export function useExamController() {
  const { id } = useParams();
  const { notify } = useApp();
  const [exam, setExam] = useState(null);
  const [answers, setAnswers] = useState({});
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const [now, setNow] = useState(Date.now());
  const nav = useNavigate();
  const sending = useRef(false);
  const expired = exam && new Date(exam.expiresAt).getTime() <= now;
  async function start() {
    setBusy(true);
    setError("");
    try {
      const r = await examApi.start(id);
      setExam(r.data);
      const stored = JSON.parse(
        sessionStorage.getItem(`exam-${r.data.sessionId}`) || "{}",
      );
      setAnswers(stored);
    } catch (e) {
      setError(message(e));
    } finally {
      setBusy(false);
    }
  }
  async function submit() {
    if (!exam || sending.current) return;
    sending.current = true;
    setBusy(true);
    try {
      const r = await examApi.submit(exam.sessionId, { answers });
      sessionStorage.removeItem(`exam-${exam.sessionId}`);
      nav(`/results/${r.data.id}`, { replace: true });
    } catch (e) {
      notify(message(e), true);
      sending.current = false;
    } finally {
      setBusy(false);
    }
  }
  const onExpired = useEffectEvent(submit);
  useEffect(() => {
    if (!exam) return;
    const timer = setInterval(() => setNow(Date.now()), 1000);
    return () => clearInterval(timer);
  }, [exam]);
  useEffect(() => {
    if (expired) onExpired();
  }, [expired]);
  function select(q, option) {
    const next = { ...answers, [q]: option };
    setAnswers(next);
    sessionStorage.setItem(`exam-${exam.sessionId}`, JSON.stringify(next));
  }
  const seconds = exam
    ? Math.max(0, Math.ceil((new Date(exam.expiresAt) - now) / 1000))
    : 0;
  return {
    exam,
    error,
    busy,
    start,
    answers,
    seconds,
    expired,
    select,
    submit,
  };
}
