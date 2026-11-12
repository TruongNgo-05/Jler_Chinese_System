import { test, expect } from "@playwright/test";
const api = "http://127.0.0.1:5173/api";
const token = "test-access-token";
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
    description: "Sách học tiếng Trung",
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
    description: "Giao tiếp hàng ngày",
    price: 200000,
    stock: 0,
    categoryIds: [2],
    categories: ["Giao tiếp"],
    imageUrl: "",
    images: [],
  },
];
async function mocks(page, user = null) {
  if (user)
    await page.addInitScript(
      ({ token }) => {
        localStorage.setItem("accessToken", token);
        localStorage.setItem("refreshToken", "test-refresh");
      },
      { token },
    );
  await page.route(`${api}/**`, async (route) => {
    const u = new URL(route.request().url());
    const p = u.pathname;
    let data = [];
    if (p === "/api/platform/me") data = user || student;
    else if (p === "/api/platform/catalog/books") data = books;
    else if (p === "/api/platform/catalog/categories")
      data = [
        { id: 1, name: "HSK" },
        { id: 2, name: "Giao tiếp" },
      ];
    else if (p === "/api/platform/catalog/books/1") data = books[0];
    else if (p === "/api/auth/login")
      data = { accessToken: token, refreshToken: "test-refresh" };
    else if (p === "/api/platform/cart") data = { items: [], subtotal: 0 };
    else if (p === "/api/platform/admin/dashboard")
      data = {
        users: 2,
        books: 2,
        courses: 1,
        orders: 3,
        pendingOrders: 1,
        revenue: 500000,
        lowStock: [],
      };
    await route.fulfill({
      json: data,
      headers: { "Access-Control-Allow-Origin": "*" },
    });
  });
}
test("catalog searches and filters real API response", async ({ page }) => {
  await mocks(page);
  await page.goto("/books");
  await expect(
    page.getByRole("link", { name: "Giáo trình HSK 1", exact: true }),
  ).toBeVisible();
  await page
    .getByRole("textbox", { name: "Tìm kiếm", exact: true })
    .fill("giao tiếp");
  await expect(
    page.getByRole("link", { name: "Giáo trình HSK 1", exact: true }),
  ).toHaveCount(0);
  await expect(
    page.getByRole("button", { name: "Tạm hết hàng" }),
  ).toBeDisabled();
});
test("protected pages redirect guests", async ({ page }) => {
  await mocks(page);
  await page.goto("/orders");
  await expect(page).toHaveURL(/\/login/);
  await expect(
    page.getByRole("heading", { name: "Chào mừng trở lại." }),
  ).toBeVisible();
});
test("login stores session and loads profile", async ({ page }) => {
  await mocks(page);
  await page.goto("/login");
  await page.getByLabel("Tên đăng nhập").fill("student");
  await page.getByLabel("Mật khẩu", { exact: true }).fill("Student123!");
  await page.getByRole("button", { name: "Đăng nhập", exact: true }).click();
  await expect(page).toHaveURL("/");
  await expect(page.locator("summary")).toContainText(student.fullName);
  expect(await page.evaluate(() => localStorage.getItem("accessToken"))).toBe(
    token,
  );
});
test("student cannot view administration", async ({ page }) => {
  await mocks(page, student);
  await page.goto("/admin/dashboard");
  await expect(
    page.getByText("Bạn không có quyền truy cập trang quản trị."),
  ).toBeVisible();
  await expect(page.getByText("Doanh thu đã thu")).toHaveCount(0);
});
test("administrator sees dashboard", async ({ page }) => {
  await mocks(page, { ...student, role: "ADMIN" });
  await page.goto("/admin/dashboard");
  await expect(page.getByText("Doanh thu đã thu")).toBeVisible();
  await expect(
    page.getByRole("link", { name: "Mã giảm giá", exact: true }),
  ).toBeVisible();
});
test("empty cart has a useful next step", async ({ page }) => {
  await mocks(page, student);
  await page.goto("/cart");
  await expect(page.getByText("Giỏ hàng đang trống.")).toBeVisible();
  await expect(
    page.getByRole("link", { name: "Khám phá hiệu sách →" }),
  ).toHaveAttribute("href", "/books");
});
test("product detail sends item to backend", async ({ page }) => {
  await mocks(page, student);
  await page.goto("/books/1");
  const request = page.waitForRequest(
    (r) => r.url() === `${api}/platform/cart` && r.method() === "POST",
  );
  await page
    .getByRole("button", { name: "Thêm vào giỏ hàng", exact: true })
    .click();
  expect((await request).postDataJSON()).toEqual({
    itemId: 1,
    itemType: "Book",
    quantity: 1,
  });
  await expect(page.getByRole("status")).toContainText("Đã thêm vào giỏ hàng");
});
test("API failures show retry", async ({ page }) => {
  await mocks(page);
  await page.route(`${api}/platform/catalog/books`, (r) =>
    r.fulfill({
      status: 500,
      json: { message: "Tạm thời chưa tải được sách" },
    }),
  );
  await page.goto("/books");
  await expect(page.getByRole("alert")).toContainText(
    "Tạm thời chưa tải được sách",
  );
  await expect(page.getByRole("button", { name: "Thử lại" })).toBeVisible();
});
test("mobile navigation and page fit viewport", async ({ page }) => {
  await page.setViewportSize({ width: 390, height: 844 });
  await mocks(page);
  await page.goto("/");
  await page.getByRole("button", { name: "Mở menu" }).click();
  await expect(
    page.locator(".main-nav").getByRole("link", { name: "Sách & giáo trình" }),
  ).toBeVisible();
  await page
    .locator(".main-nav")
    .getByRole("link", { name: "Sách & giáo trình" })
    .click();
  await expect(page).toHaveURL("/books");
  expect(
    await page.evaluate(
      () => document.documentElement.scrollWidth <= window.innerWidth,
    ),
  ).toBe(true);
});
