import { useState, useEffect } from "react";
import { authApi } from "@/features/auth/api/authApi";
import { message } from "@/shared/utils/errors";

export function useSession(notify) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    if (localStorage.getItem("accessToken"))
      authApi
        .me()
        .then((r) => setUser(r.data))
        .catch(() => {
          localStorage.removeItem("accessToken");
          localStorage.removeItem("refreshToken");
        })
        .finally(() => setLoading(false));
    else queueMicrotask(() => setLoading(false));
    const expired = () => {
      setUser(null);
      notify("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", true);
    };
    window.addEventListener("auth-expired", expired);
    return () => window.removeEventListener("auth-expired", expired);
  }, [notify]);
  async function login(data) {
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);
    try {
      const r = await authApi.me();
      setUser(r.data);
      return r.data;
    } catch (e) {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      throw e;
    }
  }
  async function logout() {
    const refreshToken = localStorage.getItem("refreshToken");
    try {
      if (refreshToken) await authApi.logout(refreshToken);
    } catch (e) {
      notify(message(e), true);
    } finally {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      setUser(null);
    }
  }
  return { user, setUser, loading, login, logout };
}
