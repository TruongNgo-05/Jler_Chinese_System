import { useData } from "@/shared/hooks/useData";
import { vocabularyApi } from "@/features/vocabulary/api/vocabularyApi";
import { useApp } from "@/shared/context/AppContext";
import { useState } from "react";
import { message } from "@/shared/utils/errors";

export function useVocabularyController() {
  const d = useData(vocabularyApi.path);
  const { notify } = useApp();
  const [form, setForm] = useState({ front: "", back: "" });
  const [busy, setBusy] = useState(false);
  const [filter, setFilter] = useState("");
  async function save(e) {
    e.preventDefault();
    setBusy(true);
    try {
      await vocabularyApi.add(form);
      setForm({ front: "", back: "" });
      d.reload();
      notify("Đã lưu từ mới");
    } catch (e) {
      notify(message(e), true);
    } finally {
      setBusy(false);
    }
  }
  async function update(word, remove = false) {
    setBusy(true);
    try {
      if (remove) await vocabularyApi.remove(word.id);
      else
        await vocabularyApi.update(word.id, {
          ...word,
          mastered: !word.mastered,
        });
      d.reload();
    } catch (e) {
      notify(message(e), true);
    } finally {
      setBusy(false);
    }
  }
  return { save, form, setForm, busy, filter, setFilter, d, update };
}
