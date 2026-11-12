import { beforeEach, expect, it, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { Cart } from "../src/features/cart/pages/CartPage";
import { AppContext } from "../src/shared/context/AppContext";
import { api } from "../src/shared/api/http";
vi.mock("../src/shared/api/http", async (original) => ({
  ...(await original()),
  api: { get: vi.fn(), post: vi.fn(), delete: vi.fn() },
}));
const item = {
  id: 11,
  itemId: 1,
  itemType: "Book",
  title: "Sách thử",
  price: 50000,
  quantity: 1,
  maxQuantity: 3,
  available: true,
};
function snapshot(i) {
  return { items: [i], subtotal: i.price * i.quantity, canCheckout: true };
}
function open(i = item) {
  api.get.mockResolvedValue({ data: snapshot(i) });
  render(
    <MemoryRouter>
      <AppContext.Provider value={{ notify: vi.fn() }}>
        <Cart />
      </AppContext.Provider>
    </MemoryRouter>,
  );
}
beforeEach(() => vi.clearAllMocks());
it("waits for quantity acknowledgement and uses returned snapshot for repeated increments", async () => {
  let finish;
  api.post
    .mockImplementationOnce(
      () =>
        new Promise((resolve) => {
          finish = resolve;
        }),
    )
    .mockResolvedValueOnce({ data: snapshot({ ...item, quantity: 3 }) });
  open();
  const plus = await screen.findByRole("button", { name: "Tăng số lượng" });
  await userEvent.click(plus);
  expect(plus).toBeDisabled();
  expect(api.post).toHaveBeenCalledWith("/platform/cart/11/adjust", {
    delta: 1,
  });
  finish({ data: snapshot({ ...item, quantity: 2 }) });
  await waitFor(() => expect(plus).toBeEnabled());
  await userEvent.click(plus);
  await waitFor(() => expect(plus).toBeDisabled());
  expect(api.post).toHaveBeenCalledTimes(2);
  expect(screen.getByText("3")).toBeInTheDocument();
  expect(api.get).toHaveBeenCalledTimes(1);
});
it("explains one-time course access without disabled quantity controls", async () => {
  open({ ...item, itemType: "Course" });
  expect(await screen.findByText("1 quyền truy cập")).toBeInTheDocument();
  expect(screen.queryByRole("button", { name: "Tăng số lượng" })).toBeNull();
});
it("keeps stock errors visible and allows a retry", async () => {
  api.post.mockRejectedValue({
    response: { data: { message: "Không đủ tồn kho" } },
  });
  open();
  const plus = await screen.findByRole("button", { name: "Tăng số lượng" });
  await userEvent.click(plus);
  await waitFor(() => expect(plus).toBeEnabled());
  expect(screen.getByText("1")).toBeInTheDocument();
});
