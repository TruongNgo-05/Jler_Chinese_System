import { useManagementController } from "@/features/admin/hooks/useManagementController";
import { Heading } from "@/shared/components/PageHeading";
import { schemas } from "@/features/admin/config/schemas";
import { State } from "@/shared/components/AsyncState";
import { money } from "@/shared/utils/format";
import { adminApi } from "@/features/admin/api/adminApi";
import { message } from "@/shared/utils/errors";
import { Empty } from "@/shared/components/EmptyState";
import { Pager } from "@/shared/components/Pagination";
import { Editor } from "@/features/admin/components/EntityEditor";

export function Management({ kind }) {
  const {
    title,
    setEditing,
    query,
    setQuery,
    setPage,
    d,
    filtered,
    page,
    busy,
    setBusy,
    notify,
    remove,
    editing,
  } = useManagementController({ kind });
  return (
    <>
      <Heading
        title={title || "Không tìm thấy mục quản lý"}
        eyebrow="JLer / QUẢN TRỊ"
      >
        {schemas[kind] && (
          <button className="button" onClick={() => setEditing({})}>
            + Thêm mới
          </button>
        )}
      </Heading>
      <div className="filters">
        <input
          aria-label="Tìm trong danh sách"
          value={query}
          placeholder="Tìm kiếm…"
          onChange={(e) => {
            setQuery(e.target.value);
            setPage(1);
          }}
        />
        <button onClick={d.reload}>Làm mới</button>
      </div>
      <State {...d} retry={d.reload} />
      {d.data && (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Nội dung</th>
                <th>Chi tiết</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {filtered.slice((page - 1) * 10, page * 10).map((x) => (
                <tr key={x.id}>
                  <td>#{x.id}</td>
                  <td>
                    <b>
                      {x.title || x.name || x.fullName || x.code || x.front}
                    </b>
                    {x.email && <small>{x.email}</small>}
                  </td>
                  <td>
                    {x.price != null && (
                      <span>{money(x.discountPrice ?? x.price)}</span>
                    )}
                    {x.stock != null && <small>Tồn: {x.stock}</small>}
                    {x.role && <span>{x.role}</span>}
                    {x.type && <span>{x.type}</span>}
                    {x.questionCount != null && (
                      <span>
                        {x.questionCount} câu · {x.timeLimit} phút
                      </span>
                    )}
                    {x.active != null && (
                      <small>
                        {x.active ? "Đang hoạt động" : "Đã ẩn / khóa"}
                      </small>
                    )}
                    {x.back && <span>{x.back}</span>}
                    {x.usedCount != null && (
                      <small>
                        Đã dùng {x.usedCount}/{x.maxUsage}
                      </small>
                    )}
                  </td>
                  <td className="actions">
                    <button onClick={() => setEditing(x)}>Sửa</button>
                    {kind === "quizzes" && (
                      <>
                        <button
                          disabled={busy === x.id}
                          onClick={async () => {
                            setBusy(x.id);
                            try {
                              await adminApi.quizVisibility(x.id, {
                                active: !x.active,
                              });
                              await d.reload();
                            } catch (e) {
                              notify(message(e), true);
                            } finally {
                              setBusy(null);
                            }
                          }}
                        >
                          {x.active ? "Ẩn đề" : "Hiện đề"}
                        </button>
                        <button
                          disabled={busy === x.id}
                          onClick={async () => {
                            setBusy(x.id);
                            try {
                              await adminApi.duplicateQuiz(x.id);
                              await d.reload();
                              notify("Đã tạo bản sao ở trạng thái ẩn");
                            } catch (e) {
                              notify(message(e), true);
                            } finally {
                              setBusy(null);
                            }
                          }}
                        >
                          Nhân bản
                        </button>
                      </>
                    )}
                    {kind !== "users" && (
                      <button
                        disabled={busy === x.id}
                        onClick={() => remove(x)}
                      >
                        Xóa
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {filtered.length === 0 && <Empty>Không có kết quả.</Empty>}
        </div>
      )}
      <Pager page={page} setPage={setPage} total={filtered.length} size={10} />
      {editing && (
        <Editor
          kind={kind}
          item={editing}
          close={() => setEditing(null)}
          done={() => {
            setEditing(null);
            d.reload();
            notify("Đã lưu thay đổi");
          }}
        />
      )}
    </>
  );
}
