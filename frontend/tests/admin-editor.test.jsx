import { beforeEach, expect, it, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { Editor } from "../src/features/admin/components/EntityEditor";
import { api } from "../src/shared/api/http";
vi.mock("../src/shared/api/http", () => ({
  api: { get: vi.fn(), put: vi.fn(), post: vi.fn() },
}));
beforeEach(() => {
  vi.clearAllMocks();
  api.get.mockResolvedValue({ data: [] });
  api.put.mockResolvedValue({ data: {} });
  api.post.mockResolvedValue({ data: {} });
});
it("updates an existing admin record through PUT after the editor was split", async () => {
  const done = vi.fn();
  render(
    <Editor
      kind="categories"
      item={{ id: 3, name: "HSK" }}
      close={vi.fn()}
      done={done}
    />,
  );
  const input = screen.getByRole("textbox", { name: "Tên danh mục" });
  await userEvent.clear(input);
  await userEvent.type(input, "HSK 1");
  await userEvent.click(screen.getByRole("button", { name: "Lưu thay đổi" }));
  await waitFor(() =>
    expect(api.put).toHaveBeenCalledWith(
      "/platform/admin/categories/3",
      expect.objectContaining({ name: "HSK 1" }),
    ),
  );
  expect(api.post).not.toHaveBeenCalled();
  expect(done).toHaveBeenCalledOnce();
});
