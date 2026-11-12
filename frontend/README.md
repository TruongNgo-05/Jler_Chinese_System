# Frontend

Giao diện JLer Chinese System được xây dựng bằng React, Vite và JavaScript. Thư mục `src/features/` tổ chức các màn hình và logic theo nghiệp vụ; `src/shared/` chứa thành phần và tiện ích dùng chung.

## Yêu cầu

Phiên bản Node.js được hỗ trợ được khai báo trong trường `engines` của `package.json`; cài npm tương ứng.

## Cài đặt và chạy local

Tại thư mục `frontend/`, chạy:

```powershell
npm ci
npm run dev
```

Mặc định, frontend gọi API qua `/api`. Cấu hình Vite chuyển tiếp `/api` và `/uploads` tới backend tại `http://127.0.0.1:8080`, vì vậy hãy chạy backend trước hoặc đồng thời.

Để dùng một API host khác, đặt `VITE_API_URL` trong tệp local `frontend/.env` (ví dụ `https://api.example.com/api`) và khởi động lại Vite. Biến có tiền tố `VITE_` được đóng gói vào mã phía trình duyệt; tuyệt đối không đặt mật khẩu, API key bí mật hoặc thông tin database vào đó.

## Lệnh kiểm tra

```powershell
npm run lint
npm test
npm run test:browser
npm run build
```

`npm run test:browser` chạy Playwright và có thể cần cài browser của Playwright trước lần chạy đầu. Bản build production được tạo tại `dist/`; chạy thử bản build bằng `npm run preview`.

## Cấu trúc chính

- `src/app/`: router, layout và provider cấp ứng dụng.
- `src/config/`: cấu hình môi trường frontend.
- `src/features/`: chức năng theo module nghiệp vụ.
- `src/shared/`: HTTP client, UI và tiện ích dùng chung.
- `src/assets/` và `public/`: tài nguyên tĩnh.
- `tests/`: kiểm thử giao diện và hành vi.

Không commit `node_modules/`, `dist/`, báo cáo kiểm thử hoặc tệp `.env` local.
