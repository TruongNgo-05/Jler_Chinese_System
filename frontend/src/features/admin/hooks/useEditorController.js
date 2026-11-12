import { useState } from "react";
import { useData } from "@/shared/hooks/useData";
import { adminApi } from "@/features/admin/api/adminApi";
import { message } from "@/shared/utils/errors";
import { uploadApi } from "@/shared/api/uploadApi";

export function useEditorController({ kind, item, done }) {
  const [form, setForm] = useState({
    active: true,
    price: 0,
    stock: 0,
    passScore: 60,
    timeLimit: 30,
    maxAttempts: 0,
    role: "USER",
    type: kind === "posts" ? "BLOG" : "PERCENT",
    maxUsage: 100,
    minOrder: 0,
    questions: [],
    categoryIds: [],
    ...item,
  });
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const categories = useData(adminApi.listPath("categories"));
  const courses = useData(adminApi.listPath("courses"));
  const chapters = useData(adminApi.listPath("chapters"));
  const lessons = useData(adminApi.listPath("lessons"));
  const options = {
    categories: categories.data,
    courses: courses.data,
    chapters: chapters.data,
    lessons: lessons.data,
  };
  function set(key, value) {
    setForm({ ...form, [key]: value });
  }
  async function submit(e) {
    e.preventDefault();
    setBusy(true);
    setError("");
    try {
      await adminApi.save(kind, item?.id, form);
      done();
    } catch (e) {
      setError(message(e));
    } finally {
      setBusy(false);
    }
  }
  async function upload(e, key) {
    const f = e.target.files[0];
    if (!f) return;
    setBusy(true);
    try {
      const fd = new FormData();
      fd.append("file", f);
      const r = await uploadApi.image(fd);
      set(key, r.data);
    } catch (e) {
      setError(message(e));
    } finally {
      setBusy(false);
    }
  }
  return { submit, form, set, options, upload, error, busy };
}
