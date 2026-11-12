import { useState } from "react";
import { useApp } from "@/shared/context/AppContext";
import { useNavigate, useSearchParams } from "react-router-dom";
import { authApi } from "@/features/auth/api/authApi";
import { message } from "@/shared/utils/errors";

export function useAuthController({ mode = "login" }) {
  const [form, setForm] = useState({});
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const { login, user } = useApp();
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const titles = {
    login: "Chào mừng trở lại.",
    register: "Bắt đầu cùng JLer.",
    forgot: "Quên mật khẩu?",
    reset: "Đặt lại mật khẩu.",
  };
  async function submit(e) {
    e.preventDefault();
    setBusy(true);
    setError("");
    try {
      if (mode === "login") {
        const r = await authApi.login(form);
        const profile = await login(r.data);
        navigate(profile.role === "ADMIN" ? "/admin/dashboard" : "/", {
          replace: true,
        });
      } else if (mode === "register") {
        if (form.passWord !== form.confirmPassword) {
          setError("Mật khẩu xác nhận không khớp");
          return;
        }
        await authApi.register({
          ...form,
          userName: form.userName.trim(),
          email: form.email.trim(),
          fullName: form.fullName.trim(),
          phone: form.phone.trim(),
        });
        setSuccess("Đăng ký thành công. Bạn có thể đăng nhập ngay.");
      } else if (mode === "forgot") {
        const r = await authApi.forgotPassword(form);
        setSuccess(r.data.message);
      } else {
        await authApi.resetPassword({
          ...form,
          token: params.get("token") || "",
        });
        setSuccess("Đã đặt lại mật khẩu. Hãy đăng nhập bằng mật khẩu mới.");
      }
    } catch (e) {
      setError(message(e));
    } finally {
      setBusy(false);
    }
  }
  const fields =
    mode === "register"
      ? [
          ["fullName", "Họ tên", "text"],
          ["userName", "Tên đăng nhập", "text"],
          ["email", "Email", "email"],
          ["phone", "Số điện thoại", "tel"],
          ["passWord", "Mật khẩu", "password"],
          ["confirmPassword", "Xác nhận mật khẩu", "password"],
        ]
      : mode === "login"
        ? [
            ["userName", "Tên đăng nhập", "text"],
            ["passWord", "Mật khẩu", "password"],
          ]
        : mode === "forgot"
          ? [["email", "Email đã đăng ký", "email"]]
          : [
              ["password", "Mật khẩu mới", "password"],
              ["confirmPassword", "Xác nhận mật khẩu", "password"],
            ];
  return { user, titles, success, submit, fields, form, setForm, error, busy };
}
