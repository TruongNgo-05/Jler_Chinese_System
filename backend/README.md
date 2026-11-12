# Backend

Backend cung cấp API cho JLer Chinese System, xây dựng bằng Java 17+, Spring Boot và Maven. Mặc định server chạy tại cổng `8080`.

## Yêu cầu

- Java 17 trở lên
- MySQL 8

## Cấu hình

Spring Boot nạp `backend/.env` khi khởi động. Tạo file này từ mẫu nếu chưa có:

```powershell
Copy-Item .env.example .env
```

Chỉ chạy lệnh trên khi chưa có `.env`; nếu file đã tồn tại, giữ lại và cập nhật thủ công để không ghi đè cấu hình local. Điền giá trị phù hợp trong `.env`:

| Biến | Mục đích |
| --- | --- |
| `DB_URL` | JDBC URL của database MySQL |
| `DB_USERNAME` | Tài khoản MySQL |
| `DB_PASSWORD` | Mật khẩu MySQL |
| `JWT_SECRET` | Khóa ký JWT riêng, tối thiểu 32 bytes |
| `ADMIN_PASSWORD` | Mật khẩu quản trị riêng, tối thiểu 12 ký tự |

Các biến khác như `SERVER_PORT`, `UPLOAD_DIR`, `FRONTEND_URL`, `CORS_ORIGINS`, `DDL_AUTO`, `SEED_DEMO`, `MAIL_*` và `SEPAY_*` cũng có trong `.env.example`. Không commit `.env` hoặc dùng thông tin ví dụ trên môi trường production; `.env` đã được thêm vào `.gitignore`.

## Database

Tệp khởi tạo nằm ở `../database/jler_full_mysql.sql`. Import tệp này vào database rỗng bằng MySQL Workbench. Không import lại lên database đang có dữ liệu vì có thể làm thay đổi dữ liệu hiện tại.

## Chạy ứng dụng

Mở PowerShell tại thư mục `backend/`:

```powershell
.\mvnw.cmd spring-boot:run
```

Hoặc mở `pom.xml` trong IDE và chạy lớp `com.example.jler_chinese_system.JlerChineseSystemApplication`. Đặt working directory là thư mục `backend/` để thư mục `uploads/` mặc định được giải quyết đúng.

## Kiểm thử và đóng gói

```powershell
.\mvnw.cmd clean verify
```

Maven lưu kết quả build trong `target/`. Không commit thư mục này. Kiểm thử sử dụng cấu hình riêng trong `src/test/resources`; không cần dùng database production để chạy bộ test.

## Upload và media

`UPLOAD_DIR` mặc định là `uploads` tương đối với working directory. Tệp người dùng tải lên được lưu tại đây và không nên đưa lên Git. Các media mẫu cần thiết cho dữ liệu demo nằm trong `uploads/jler-media/` và được giữ trong repository.
