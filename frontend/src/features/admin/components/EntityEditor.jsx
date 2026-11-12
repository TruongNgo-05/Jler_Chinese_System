import { useEditorController } from "@/features/admin/hooks/useEditorController";
import { Modal } from "@/shared/components/Modal";
import { adminMenu as menus } from "@/features/admin/config/menu";
import { schemas } from "@/features/admin/config/schemas";
import { Questions } from "@/features/admin/components/QuestionEditor";

export function Editor({ kind, item, close, done }) {
  const { submit, form, set, options, upload, error, busy } =
    useEditorController({ kind, item, close, done });
  return (
    <Modal
      title={`${item?.id ? "Cập nhật" : "Thêm"} ${menus.find((x) => x[0] === kind)?.[1] || ""}`}
      close={close}
    >
      <form onSubmit={submit}>
        {schemas[kind]?.map(([key, label, type = "text"]) => {
          if (kind === "users" && item?.id && key === "password") return null;
          const optional =
            label.includes("tùy chọn") || label.includes("bỏ trống");
          if (type === "questions")
            return (
              <div key={key}>
                <h3>{label}</h3>
                <Questions
                  value={form.questions}
                  onChange={(v) => set(key, v)}
                />
              </div>
            );
          if (type === "categories")
            return (
              <fieldset key={key}>
                <legend>{label}</legend>
                {options.categories?.map((c) => (
                  <label className="check-label" key={c.id}>
                    <input
                      type="checkbox"
                      checked={form.categoryIds.includes(c.id)}
                      onChange={(e) =>
                        set(
                          key,
                          e.target.checked
                            ? [...form.categoryIds, c.id]
                            : form.categoryIds.filter((id) => id !== c.id),
                        )
                      }
                    />
                    {c.name}
                  </label>
                ))}
              </fieldset>
            );
          let opts =
            type === "role"
              ? [
                  ["USER", "Người dùng"],
                  ["ADMIN", "Quản trị"],
                ]
              : type === "postType"
                ? [
                    ["BLOG", "Bài viết"],
                    ["BANNER", "Banner"],
                    ["NOTICE", "Thông báo"],
                  ]
                : type === "discountType"
                  ? [
                      ["PERCENT", "Phần trăm"],
                      ["FIXED", "Số tiền"],
                    ]
                  : options[type]?.map((x) => [x.id, x.title]);
          return (
            <label
              key={key}
              className={type === "checkbox" ? "check-label" : ""}
            >
              {type === "checkbox" && (
                <input
                  type="checkbox"
                  checked={!!form[key]}
                  onChange={(e) => set(key, e.target.checked)}
                />
              )}
              <span>{label}</span>
              {type === "checkbox" ? null : opts ? (
                <select
                  required={!optional}
                  value={form[key] ?? ""}
                  onChange={(e) => set(key, e.target.value)}
                >
                  <option value="">Chọn…</option>
                  {opts.map(([v, l]) => (
                    <option key={v} value={v}>
                      {l}
                    </option>
                  ))}
                </select>
              ) : type === "textarea" || type === "lines" ? (
                <textarea
                  required={!optional && type !== "lines"}
                  value={
                    type === "lines"
                      ? (form[key] || []).join("\n")
                      : form[key] || ""
                  }
                  onChange={(e) =>
                    set(
                      key,
                      type === "lines"
                        ? e.target.value.split("\n")
                        : e.target.value,
                    )
                  }
                />
              ) : (
                <>
                  <input
                    type={type === "image" ? "text" : type}
                    required={
                      !optional &&
                      type !== "image" &&
                      !(kind === "users" && key === "address") &&
                      !(kind === "lessons" && key === "content")
                    }
                    min={type === "number" ? 0 : undefined}
                    step={type === "number" ? "0.01" : undefined}
                    maxLength={type === "password" ? 72 : 1000}
                    readOnly={
                      kind === "users" && key === "userName" && !!item?.id
                    }
                    value={form[key] ?? ""}
                    onChange={(e) => set(key, e.target.value)}
                  />
                  {type === "image" && (
                    <input
                      aria-label={`Tải ${label}`}
                      type="file"
                      accept="image/jpeg,image/png"
                      onChange={(e) => upload(e, key)}
                    />
                  )}
                </>
              )}
            </label>
          );
        })}
        {error && (
          <p className="error" role="alert">
            {error}
          </p>
        )}
        <div className="form-actions">
          <button type="button" onClick={close}>
            Đóng
          </button>
          <button className="button" disabled={busy}>
            {busy ? "Đang lưu…" : "Lưu thay đổi"}
          </button>
        </div>
      </form>
    </Modal>
  );
}
