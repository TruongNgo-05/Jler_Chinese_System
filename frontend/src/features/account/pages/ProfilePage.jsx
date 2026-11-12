import { useProfileController } from "@/features/account/hooks/useProfileController";
import { Heading } from "@/shared/components/PageHeading";
import { Image } from "@/shared/components/MediaImage";

export function Profile() {
  const {
    save,
    form,
    avatar,
    user,
    setForm,
    busy,
    change,
    password,
    setPassword,
  } = useProfileController();
  return (
    <section className="section">
      <Heading title="Tài khoản của bạn" eyebrow="GÓC CÁ NHÂN" />
      <div className="profile-grid">
        <form className="panel" onSubmit={save}>
          <h2>Thông tin cá nhân</h2>
          <div className="avatar">
            <Image src={form.avatar} alt="Ảnh đại diện" />
          </div>
          <label>
            Chọn ảnh đại diện
            <input
              type="file"
              accept="image/png,image/jpeg"
              onChange={avatar}
            />
          </label>
          <p className="muted">
            {user.userName} · {user.email}
          </p>
          {[
            ["fullName", "Họ tên"],
            ["phone", "Điện thoại"],
            ["address", "Địa chỉ"],
          ].map(([key, label]) => (
            <label key={key}>
              {label}
              <input
                required={key === "fullName"}
                maxLength="255"
                value={form[key] || ""}
                onChange={(e) => setForm({ ...form, [key]: e.target.value })}
              />
            </label>
          ))}
          <button className="button" disabled={busy}>
            Lưu hồ sơ
          </button>
        </form>
        <form className="panel" onSubmit={change}>
          <h2>Đổi mật khẩu</h2>
          {[
            ["oldPassword", "Mật khẩu hiện tại"],
            ["newPassword", "Mật khẩu mới"],
            ["confirmPassword", "Xác nhận mật khẩu"],
          ].map(([key, label]) => (
            <label key={key}>
              {label}
              <input
                type="password"
                required
                minLength={key === "oldPassword" ? 1 : 8}
                maxLength="72"
                autoComplete={
                  key === "oldPassword" ? "current-password" : "new-password"
                }
                value={password[key] || ""}
                onChange={(e) =>
                  setPassword({ ...password, [key]: e.target.value })
                }
              />
            </label>
          ))}
          <button className="button" disabled={busy}>
            Đổi mật khẩu
          </button>
        </form>
      </div>
    </section>
  );
}
