import { useApp } from "@/shared/context/AppContext";
import { useState } from "react";
import { accountApi } from "@/features/account/api/accountApi";
import { message } from "@/shared/utils/errors";
import { uploadApi } from "@/shared/api/uploadApi";

export function useProfileController() {
  const { user, setUser, notify } = useApp();
  const [form, setForm] = useState({ ...user });
  const [password, setPassword] = useState({});
  const [busy, setBusy] = useState(false);
  async function save(e) {
    e.preventDefault();
    setBusy(true);
    try {
      const r = await accountApi.update(form);
      setUser(r.data);
      notify("Đã cập nhật hồ sơ");
    } catch (e) {
      notify(message(e), true);
    } finally {
      setBusy(false);
    }
  }
  async function change(e) {
    e.preventDefault();
    setBusy(true);
    try {
      await accountApi.changePassword(password);
      setPassword({});
      notify("Đã đổi mật khẩu");
    } catch (e) {
      notify(message(e), true);
    } finally {
      setBusy(false);
    }
  }
  async function avatar(e) {
    const file = e.target.files[0];
    if (!file) return;
    if (file.size > 10 * 1024 * 1024) {
      notify("Ảnh tối đa 10MB", true);
      return;
    }
    setBusy(true);
    try {
      const body = new FormData();
      body.append("file", file);
      const r = await uploadApi.image(body);
      setForm({ ...form, avatar: r.data });
      notify("Ảnh đã tải lên. Bấm lưu hồ sơ để áp dụng.");
    } catch (e) {
      notify(message(e), true);
    } finally {
      setBusy(false);
    }
  }
  return {
    save,
    form,
    avatar,
    user,
    setForm,
    busy,
    change,
    password,
    setPassword,
  };
}
