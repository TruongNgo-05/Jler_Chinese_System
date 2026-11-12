import { beforeEach, expect, it, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { Checkout } from "../src/features/checkout/pages/CheckoutPage";
import { AppContext } from "../src/shared/context/AppContext";
import { api } from "../src/shared/api/http";
vi.mock("../src/shared/api/http", () => ({
  api: { get: vi.fn(), post: vi.fn() },
}));
const user = {
  id: 7,
  fullName: "Người nhận",
  phone: "0912345678",
  address: "Hà Nội",
};
beforeEach(() => {
  vi.clearAllMocks();
  sessionStorage.clear();
  api.get.mockImplementation(async (path) => {
    if (path === "/platform/cart")
      return {
        data: {
          items: [
            {
              id: 1,
              itemType: "Book",
              title: "Giáo trình HSK",
              quantity: 1,
              price: 120000,
            },
          ],
          subtotal: 120000,
          canCheckout: true,
        },
      };
    if (path === "/platform/payment-options")
      return { data: { sepayEnabled: true } };
    throw { response: { data: { message: "Mã không hợp lệ" } } };
  });
});
function open() {
  render(
    <MemoryRouter initialEntries={["/checkout"]}>
      <AppContext.Provider value={{ user, notify: vi.fn() }}>
        <Routes>
          <Route path="/checkout" element={<Checkout />} />
          <Route path="/orders" element={<h1>Đã tới đơn hàng</h1>} />
        </Routes>
      </AppContext.Provider>
    </MemoryRouter>,
  );
}
it("does not send a rejected voucher when placing an order", async () => {
  api.post.mockResolvedValue({ data: { id: 99 } });
  open();
  const input = await screen.findByPlaceholderText("Nhập mã");
  await userEvent.type(input, "INVALID");
  await userEvent.click(screen.getByRole("button", { name: "Áp dụng" }));
  const submit = screen.getByRole("button", { name: "Xác nhận đặt hàng" });
  await waitFor(() => expect(submit).toBeEnabled());
  await userEvent.click(submit);
  await waitFor(() =>
    expect(api.post).toHaveBeenCalledWith(
      "/platform/orders",
      expect.objectContaining({
        voucherCode: "",
        paymentMethod: "SEPAY",
        checkoutKey: expect.any(String),
      }),
    ),
  );
  expect(
    await screen.findByRole("heading", { name: "Đã tới đơn hàng" }),
  ).toBeInTheDocument();
});
it("reuses the checkout key after a failed response to prevent duplicate orders", async () => {
  api.post
    .mockRejectedValueOnce(new Error("response lost"))
    .mockResolvedValueOnce({ data: { id: 100 } });
  open();
  const submit = await screen.findByRole("button", {
    name: "Xác nhận đặt hàng",
  });
  await waitFor(() => expect(submit).toBeEnabled());
  await userEvent.click(submit);
  await waitFor(() => expect(api.post).toHaveBeenCalledTimes(1));
  await waitFor(() => expect(submit).toBeEnabled());
  const first = api.post.mock.calls[0][1].checkoutKey;
  await userEvent.click(submit);
  await waitFor(() => expect(api.post).toHaveBeenCalledTimes(2));
  expect(api.post.mock.calls[1][1].checkoutKey).toBe(first);
});
