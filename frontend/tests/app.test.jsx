import { beforeEach, describe, expect, it, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import App from "../src/App";
import { api } from "../src/shared/api/http";
vi.mock("../src/shared/api/http", async (importOriginal) => {
  const original = await importOriginal();
  return {
    ...original,
    api: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() },
  };
});
const student = {
  id: 7,
  userName: "student",
  fullName: "Học viên JLer",
  email: "student@test.local",
  role: "USER",
  phone: "0912345678",
};
const books = [
  {
    id: 1,
    title: "Giáo trình HSK 1",
    description: "Sách tiếng Trung",
    price: 150000,
    stock: 12,
    categoryIds: [1],
    categories: ["HSK"],
    imageUrl: "",
    images: [],
  },
  {
    id: 2,
    title: "Tiếng Trung giao tiếp",
    price: 200000,
    stock: 0,
    categoryIds: [2],
    categories: ["Giao tiếp"],
    imageUrl: "",
    images: [],
  },
];
function open(path, user, posts = []) {
  if (user) localStorage.setItem("accessToken", "test-access");
  api.get.mockImplementation(async (path) => {
    let data = [];
    if (path === "/platform/me") data = user || student;
    else if (path === "/platform/catalog/books") data = books;
    else if (path === "/platform/catalog/posts") data = posts;
    else if (path === "/platform/catalog/categories")
      data = [
        { id: 1, name: "HSK" },
        { id: 2, name: "Giao tiếp" },
      ];
    else if (path === "/platform/catalog/books/1") data = books[0];
    else if (path === "/platform/cart") data = { items: [], subtotal: 0 };
    else if (path === "/platform/admin/dashboard")
      data = {
        users: 2,
        books: 2,
        courses: 1,
        orders: 3,
        pendingOrders: 1,
        revenue: 500000,
        lowStock: [],
      };
    return { data };
  });
  return render(
    <MemoryRouter initialEntries={[path]}>
      <App />
    </MemoryRouter>,
  );
}
beforeEach(() => {
  vi.clearAllMocks();
  localStorage.clear();
  sessionStorage.clear();
  api.post.mockResolvedValue({
    data: { accessToken: "test-access", refreshToken: "test-refresh" },
  });
});
describe("JLer screens", () => {
  it("searches and filters catalog products", async () => {
    open("/books");
    await screen.findByRole("link", { name: "Giáo trình HSK 1", exact: true });
    await userEvent.type(
      screen.getByRole("textbox", { name: "Tìm kiếm", exact: true }),
      "giao tiếp",
    );
    expect(
      screen.queryByRole("link", { name: "Giáo trình HSK 1", exact: true }),
    ).toBeNull();
    expect(screen.getByRole("button", { name: "Tạm hết hàng" })).toBeDisabled();
  });
  it("redirects guests from protected pages", async () => {
    open("/orders");
    expect(
      await screen.findByRole("heading", { name: "Chào mừng trở lại." }),
    ).toBeVisible();
  });
  it("logs in and loads the authenticated profile", async () => {
    open("/login");
    await userEvent.type(screen.getByLabelText("Tên đăng nhập"), "student");
    await userEvent.type(
      screen.getByLabelText("Mật khẩu", { exact: true }),
      "Student123!",
    );
    await userEvent.click(
      screen.getByRole("button", { name: "Đăng nhập", exact: true }),
    );
    expect(await screen.findByText("Học viên JLer ▾")).toBeVisible();
    expect(localStorage.getItem("accessToken")).toBe("test-access");
    expect(api.post).toHaveBeenCalledWith("/auth/login", {
      userName: "student",
      passWord: "Student123!",
    });
  });
  it("rejects student access to admin screens", async () => {
    open("/admin/dashboard", student);
    expect(
      await screen.findByText(/Bạn không có quyền truy cập trang quản trị/),
    ).toBeVisible();
    expect(api.get).not.toHaveBeenCalledWith("/platform/admin/dashboard");
  });
  it("shows live dashboard data to administrators", async () => {
    open("/admin/dashboard", { ...student, role: "ADMIN" });
    expect(await screen.findByText("Doanh thu đã thu")).toBeVisible();
    expect(screen.getByText("500.000 ₫")).toBeVisible();
  });
  it("shows an empty cart with a shopping link", async () => {
    open("/cart", student);
    expect(await screen.findByText(/Giỏ hàng đang trống/)).toBeVisible();
    expect(
      screen.getByRole("link", { name: "Khám phá hiệu sách →" }),
    ).toHaveAttribute("href", "/books");
  });
  it("adds the selected product through the cart API", async () => {
    open("/books/1", student);
    await screen.findByRole("heading", { name: "Giáo trình HSK 1" });
    await userEvent.click(
      screen.getByRole("button", { name: "Thêm vào giỏ hàng", exact: true }),
    );
    await waitFor(() =>
      expect(api.post).toHaveBeenCalledWith("/platform/cart", {
        itemId: 1,
        itemType: "Book",
        quantity: 1,
      }),
    );
    expect(await screen.findByRole("status")).toHaveTextContent(
      "Đã thêm vào giỏ hàng",
    );
  });
  it("shows an actionable loading failure", async () => {
    open("/books");
    api.get.mockRejectedValue({
      response: { data: { message: "Không tải được danh sách" } },
    });
    await userEvent.selectOptions(
      await screen.findByRole("combobox", { name: "Danh mục" }),
      "1",
    );
    await userEvent.click(
      screen.getByRole("link", { name: "Khóa học", exact: true }),
    );
    expect(await screen.findByRole("alert")).toHaveTextContent(
      "Không tải được danh sách",
    );
    expect(screen.getByRole("button", { name: "Thử lại" })).toBeVisible();
  });
  it("opens the mobile menu and navigates", async () => {
    open("/");
    const menu = screen.getByRole("button", { name: "Mở menu" });
    await userEvent.click(menu);
    expect(menu).toHaveAttribute("aria-expanded", "true");
    const links = screen.getAllByRole("link", {
      name: "Sách & giáo trình",
      exact: true,
    });
    await userEvent.click(links[0]);
    expect(
      await screen.findByRole("heading", { name: "Sách & giáo trình" }),
    ).toBeVisible();
    expect(menu).toHaveAttribute("aria-expanded", "false");
  });
  it("saves personal vocabulary", async () => {
    open("/vocabulary", student);
    await screen.findByRole("heading", { name: "Sổ từ vựng của tôi" });
    await userEvent.type(screen.getByLabelText("Từ / Pinyin"), "你好");
    await userEvent.type(screen.getByLabelText("Nghĩa tiếng Việt"), "Xin chào");
    await userEvent.click(screen.getByRole("button", { name: "+ Lưu từ" }));
    await waitFor(() =>
      expect(api.post).toHaveBeenCalledWith("/platform/words", {
        front: "你好",
        back: "Xin chào",
      }),
    );
  });
  it("uses managed banner text and images", async () => {
    open("/", null, [
      {
        id: 1,
        type: "BANNER",
        title: "Khám phá JLer",
        content: "Nội dung từ quản trị",
        imageUrl: "/uploads/first.png",
      },
      {
        id: 2,
        type: "BANNER",
        title: "Học mỗi ngày",
        content: "Thông điệp mới",
        imageUrl: "/uploads/second.png",
      },
    ]);
    expect(
      await screen.findByRole("heading", { name: "Khám phá JLer" }),
    ).toBeVisible();
    expect(screen.getByAltText("Khám phá JLer")).toHaveAttribute(
      "src",
      "/uploads/first.png",
    );
    await userEvent.click(
      screen.getByRole("button", { name: "Banner tiếp theo" }),
    );
    expect(
      await screen.findByRole("heading", { name: "Học mỗi ngày" }),
    ).toBeVisible();
  });
  it("admin login navigates directly into an independent management layout", async () => {
    open("/login");
    const fallback = api.get.getMockImplementation();
    api.get.mockImplementation(async (path) =>
      path === "/platform/me"
        ? { data: { ...student, role: "ADMIN" } }
        : fallback(path),
    );
    await userEvent.type(screen.getByLabelText("Tên đăng nhập"), "admin");
    await userEvent.type(
      screen.getByLabelText("Mật khẩu", { exact: true }),
      "AdminPassword123!",
    );
    await userEvent.click(
      screen.getByRole("button", { name: "Đăng nhập", exact: true }),
    );
    await screen.findByText("Doanh thu đã thu");
    expect(
      screen.getByRole("navigation", { name: "Điều hướng quản trị" }),
    ).toBeVisible();
    expect(document.querySelector(".site-header")).toBeNull();
    expect(document.querySelector("footer")).toBeNull();
    expect(screen.getByRole("img", { name: "JLer" })).toBeVisible();
    expect(screen.getByRole("link", { name: "Giao dịch SePay" })).toBeVisible();
  });
  it("registration validates password confirmation without calling server", async () => {
    open("/register");
    await userEvent.type(screen.getByLabelText("Họ tên"), "Người học");
    await userEvent.type(screen.getByLabelText("Tên đăng nhập"), "student2");
    await userEvent.type(
      screen.getByLabelText("Email", { exact: true }),
      "new@test.local",
    );
    await userEvent.type(screen.getByLabelText("Số điện thoại"), "0912345678");
    await userEvent.type(
      screen.getByLabelText("Mật khẩu", { exact: true }),
      "Student123!",
    );
    await userEvent.type(
      screen.getByLabelText("Xác nhận mật khẩu"),
      "Different123!",
    );
    await userEvent.click(
      screen.getByRole("button", { name: "Tạo tài khoản" }),
    );
    expect(await screen.findByRole("alert")).toHaveTextContent(
      "Mật khẩu xác nhận không khớp",
    );
    expect(api.post).not.toHaveBeenCalled();
  });
  it("registration sends correct fields and preserves server error messages", async () => {
    open("/register");
    api.post.mockRejectedValueOnce({
      response: { data: { message: "Email đã được đăng ký" } },
    });
    await userEvent.type(screen.getByLabelText("Họ tên"), "Người học");
    await userEvent.type(screen.getByLabelText("Tên đăng nhập"), "student2");
    await userEvent.type(
      screen.getByLabelText("Email", { exact: true }),
      "new@test.local",
    );
    await userEvent.type(screen.getByLabelText("Số điện thoại"), "0912345678");
    await userEvent.type(
      screen.getByLabelText("Mật khẩu", { exact: true }),
      "Student123!",
    );
    await userEvent.type(
      screen.getByLabelText("Xác nhận mật khẩu"),
      "Student123!",
    );
    await userEvent.click(
      screen.getByRole("button", { name: "Tạo tài khoản" }),
    );
    expect(await screen.findByRole("alert")).toHaveTextContent(
      "Email đã được đăng ký",
    );
    expect(api.post).toHaveBeenCalledWith("/auth/register", {
      fullName: "Người học",
      userName: "student2",
      email: "new@test.local",
      phone: "0912345678",
      passWord: "Student123!",
      confirmPassword: "Student123!",
    });
  });
  it("payment QR uses server data and never confirms payment in the browser", async () => {
    open("/orders?pay=51", student);
    const fallback = api.get.getMockImplementation();
    api.get.mockImplementation(async (path) =>
      path === "/platform/orders/51/payment"
        ? {
            data: {
              orderId: 51,
              code: "JL1234567890ABCDEF",
              method: "SEPAY",
              status: "PENDING",
              paymentStatus: "pending",
              amount: 199000,
              available: true,
              bank: "MB",
              accountNumber: "0123456789",
              accountName: "JLER TEST",
              transferContent: "JL1234567890ABCDEF",
              qrUrl: "https://vietqr.app/img?acc=0123456789&amount=199000",
            },
          }
        : fallback(path),
    );
    expect(
      await screen.findByRole("img", {
        name: "QR thanh toán đơn JL1234567890ABCDEF",
      }),
    ).toHaveAttribute(
      "src",
      "https://vietqr.app/img?acc=0123456789&amount=199000",
    );
    expect(screen.getByText("Đang chờ xác nhận từ ngân hàng…")).toBeVisible();
    expect(api.post).not.toHaveBeenCalled();
  });
  it("shows server-confirmed payment success and removes QR", async () => {
    open("/orders?pay=51", student);
    const fallback = api.get.getMockImplementation();
    api.get.mockImplementation(async (path) =>
      path === "/platform/orders/51/payment"
        ? {
            data: {
              orderId: 51,
              code: "JL1234567890ABCDEF",
              method: "SEPAY",
              status: "COMPLETED",
              paymentStatus: "paid",
              amount: 199000,
              available: false,
              qrUrl: null,
            },
          }
        : fallback(path),
    );
    expect(
      await screen.findByRole("heading", { name: "Thanh toán thành công" }),
    ).toBeVisible();
    expect(screen.queryByRole("img", { name: /QR thanh toán đơn/ })).toBeNull();
    expect(api.post).not.toHaveBeenCalled();
  });
  it("account editor only offers ADMIN and USER", async () => {
    open("/admin/users", { ...student, role: "ADMIN" });
    await userEvent.click(
      await screen.findByRole("button", { name: "+ Thêm mới" }),
    );
    const role = screen.getByLabelText("Vai trò");
    expect(
      [...role.options].filter((o) => o.value).map((o) => o.value),
    ).toEqual(["USER", "ADMIN"]);
    expect(screen.queryByRole("option", { name: "Giáo viên" })).toBeNull();
  });
});
