# JLer Chinese System

Ứng dụng học tiếng Trung gồm backend Spring Boot, frontend React/Vite và cơ sở dữ liệu MySQL.

## Thành phần

| Thư mục | Nội dung |
| --- | --- |
| `backend/` | API Spring Boot, mã nguồn Java, cấu hình và media dùng chung |
| `frontend/` | Giao diện React, Vite, kiểm thử và cấu hình triển khai |
| `database/` | Tệp SQL khởi tạo cơ sở dữ liệu |
| `backend/uploads/` | Ảnh/video tải lên khi chạy; media mẫu nằm trong `jler-media/` |

## Yêu cầu

- Java 17 trở lên
- MySQL 8
- Node.js theo phiên bản khai báo trong `frontend/package.json` và npm

## Chạy lần đầu

### 1. Cấu hình database và backend

Tạo database `jler_chinese_system` trong MySQL. Nếu database chưa có schema, import `database/jler_full_mysql.sql` bằng MySQL Workbench. Không import lại tệp này lên database đang có dữ liệu.

Backend đọc cấu hình từ biến môi trường và tự nạp `backend/.env`. Sao chép `backend/.env.example` thành `backend/.env` (chỉ khi chưa có file) rồi thay giá trị mẫu bằng cấu hình riêng. Tối thiểu cần thiết lập:

- `DB_URL`: JDBC URL của MySQL
- `DB_USERNAME` và `DB_PASSWORD`: tài khoản database
- `JWT_SECRET`: chuỗi bí mật riêng, tối thiểu 32 bytes
- `ADMIN_PASSWORD`: mật khẩu quản trị riêng, tối thiểu 12 ký tự

Có thể cần cấu hình thêm SMTP, CORS hoặc SePay; xem `backend/README.md`. Không dùng thông tin ví dụ trong `.env.example` trên môi trường chia sẻ hoặc production.

Mở PowerShell tại thư mục dự án:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Backend mặc định chạy tại `http://localhost:8080`.

### 2. Chạy frontend

Mở terminal khác tại thư mục dự án:

```powershell
cd frontend
npm ci
npm run dev
```

Mở URL Vite hiển thị trong terminal (thường là `http://localhost:5173`). Mặc định Vite chuyển tiếp `/api` và `/uploads` tới backend tại cổng 8080. Nếu đổi địa chỉ API, đặt `VITE_API_URL` trong `frontend/.env` rồi khởi động lại Vite. Không đặt secret hoặc thông tin database trong biến `VITE_*`.

## Kiểm tra và build

Chạy tại `backend/`:

```powershell
.\mvnw.cmd clean verify
```

Chạy tại `frontend/`:

```powershell
npm run lint
npm test
npm run build
```

Frontend build được tạo trong `frontend/dist/`; backend build được tạo trong `backend/target/`. Đây là các thư mục sinh tự động, không cần đưa lên Git.

## Trước khi đẩy lên Git

- Chỉ commit mã nguồn, cấu hình mẫu không chứa secret và media mẫu cần thiết.
- Không commit `.env`, mật khẩu, khóa JWT, thông tin thanh toán hoặc dữ liệu người dùng.
- Các tệp tải lên riêng được ignore; chỉ nội dung mẫu trong `backend/uploads/jler-media/` được giữ để chia sẻ.
- Kiểm tra `database/jler_full_mysql.sql` để chắc chắn không có dữ liệu cá nhân hoặc production trước khi public repository.
- `.gitignore` không bỏ theo dõi tệp đã được commit trước đó. Nếu `.env` đã được theo dõi, hãy bỏ theo dõi tệp đó khỏi Git nhưng giữ lại bản local trước khi push.

Xem hướng dẫn riêng tại [backend/README.md](backend/README.md), [frontend/README.md](frontend/README.md) và [database/README.md](database/README.md).
