# Database

Thư mục này chứa script SQL dùng để khởi tạo database MySQL cho JLer Chinese System.

## Tệp hiện có

- `jler_full_mysql.sql`: script khởi tạo schema và dữ liệu được đóng gói cùng dự án.

## Cách sử dụng

1. Tạo database rỗng tên `jler_chinese_system` trong MySQL 8.
2. Mở `jler_full_mysql.sql` bằng MySQL Workbench và chạy script trên database vừa tạo.
3. Cấu hình backend trỏ tới database đó bằng `DB_URL`, `DB_USERNAME` và `DB_PASSWORD`.

Chỉ import trên database mới/rỗng. Không chạy lại script trên database đang hoạt động hoặc đang có dữ liệu cần giữ.

## Lưu ý trước khi đẩy Git

SQL có thể chứa dữ liệu ngoài schema. Kiểm tra và loại bỏ dữ liệu cá nhân, dữ liệu production hoặc thông tin bí mật trước khi public repository. Không lưu mật khẩu hay secret kết nối database trong tệp SQL.
