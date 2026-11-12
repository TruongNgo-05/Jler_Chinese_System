import { afterEach, expect, it, vi } from "vitest";
import { act, render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { Payment } from "../src/features/payments/components/PaymentPanel";
import { api } from "../src/shared/api/http";
vi.mock("../src/shared/api/http", () => ({ api: { get: vi.fn() } }));
afterEach(() => vi.useRealTimers());
it("waits for the server's paid status before announcing SePay success", async () => {
  vi.useFakeTimers();
  const onPaid = vi.fn();
  const pending = {
    available: true,
    method: "SEPAY",
    status: "PENDING",
    paymentStatus: "pending",
    amount: 199000,
    code: "JL123",
    qrUrl: "https://example.test/qr.png",
    accountNumber: "123456",
    transferContent: "JL123",
  };
  api.get
    .mockResolvedValueOnce({ data: pending })
    .mockResolvedValueOnce({ data: { ...pending, paymentStatus: "paid" } });
  render(
    <MemoryRouter>
      <Payment orderId={12} onPaid={onPaid} />
    </MemoryRouter>,
  );
  await act(async () => {
    await Promise.resolve();
  });
  expect(screen.getByAltText("QR thanh toán đơn JL123")).toBeInTheDocument();
  expect(onPaid).not.toHaveBeenCalled();
  await act(async () => {
    await vi.advanceTimersByTimeAsync(5000);
  });
  expect(
    screen.getByRole("heading", { name: "Thanh toán thành công" }),
  ).toBeInTheDocument();
  expect(onPaid).toHaveBeenCalledOnce();
  expect(api.get).toHaveBeenCalledTimes(2);
  expect(api.get).toHaveBeenLastCalledWith("/platform/orders/12/payment");
});
