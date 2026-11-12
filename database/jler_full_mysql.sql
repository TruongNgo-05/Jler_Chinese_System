-- JLer FULL schema + demo data. Import into a NEW / EMPTY database only.
CREATE DATABASE IF NOT EXISTS jler_chinese_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE jler_chinese_system;
SET NAMES utf8mb4;

create table book_category_mapping (book_id integer not null, category_id integer not null, primary key (book_id, category_id)) engine=InnoDB;
create table book_images (book_id integer not null, book_image_id integer not null auto_increment, is_thumbnail bit, created_at datetime(6), image_url TEXT not null, primary key (book_image_id)) engine=InnoDB;
create table books (book_id integer not null auto_increment, discount_price decimal(15,2), price decimal(15,2), stock integer, description TEXT, title varchar(255) not null, primary key (book_id)) engine=InnoDB;
create table cart_items (cart_id integer not null, cart_item_id integer not null auto_increment, item_id integer not null, price_at_purchase decimal(15,2), quantity integer, created_at datetime(6), item_type varchar(20) not null, primary key (cart_item_id)) engine=InnoDB;
create table carts (cart_id integer not null auto_increment, user_id integer, created_at datetime(6), update_at datetime(6), primary key (cart_id)) engine=InnoDB;
create table categories (category_id integer not null auto_increment, name_category varchar(100) not null, primary key (category_id)) engine=InnoDB;
create table chapters (chapter_id integer not null auto_increment, course_id integer, is_active bit, order_index integer, title varchar(255) not null, primary key (chapter_id)) engine=InnoDB;
create table combo_items (combo_id integer not null, combo_item_id integer not null auto_increment, item_id integer not null, quantity integer, item_type varchar(20) not null, primary key (combo_item_id)) engine=InnoDB;
create table combos (combo_id integer not null auto_increment, combo_price decimal(15,2), original_price decimal(15,2), description TEXT, name varchar(255) not null, thumbnail_url varchar(255), primary key (combo_id)) engine=InnoDB;
create table comments (comment_id integer not null auto_increment, lesson_id integer not null, parent_id integer, user_id integer not null, created_at datetime(6), content TEXT, primary key (comment_id)) engine=InnoDB;
create table course_contents (chapter_id integer not null, course_content_id integer not null auto_increment, is_preview bit, lesson_id integer, order_index integer, quiz_id integer, content_type varchar(255) not null, primary key (course_content_id)) engine=InnoDB;
create table courses (course_id integer not null auto_increment, discount_price decimal(15,2), price decimal(15,2), total_duration integer, total_lesson integer, thumbnail_url varchar(1000), course_type varchar(255), description TEXT, status varchar(255), title varchar(255) not null, primary key (course_id)) engine=InnoDB;
create table favorite (id integer not null auto_increment, item_id integer, user_id integer, item_type varchar(255), primary key (id)) engine=InnoDB;
create table flashcards (flashcard_id integer not null auto_increment, lesson_id integer, image_url varchar(1000), back_text TEXT not null, front_text TEXT not null, primary key (flashcard_id)) engine=InnoDB;
create table lessons (duration integer, is_free bit, lesson_id integer not null auto_increment, attachment_url varchar(1000), video_url varchar(1000), content LONGTEXT, title varchar(255) not null, primary key (lesson_id)) engine=InnoDB;
create table order_items (item_id integer, order_id integer, order_item_id integer not null auto_increment, price_at_purchase decimal(15,2) not null, quantity integer, item_title varchar(255), item_type enum ('Book','Combo','Course'), primary key (order_item_id)) engine=InnoDB;
create table orders (discount decimal(15,2), has_physical_item bit, order_id integer not null auto_increment, shipping_fee decimal(15,2), subtotal decimal(15,2), total_amount decimal(15,2) not null, user_id integer, voucher_id integer, created_at datetime(6), expired_at datetime(6), payment_at datetime(6), order_code varchar(50) not null, checkout_key varchar(64), request_signature varchar(64), transaction_id varchar(100), note TEXT, payment_method varchar(255), status varchar(255), payment_status enum ('expired','failed','paid','pending'), shipping_status enum ('cancelled','delivered','none','preparing','shipping'), primary key (order_id)) engine=InnoDB;
create table password_reset (id integer not null auto_increment, used bit, user_id integer, expires_at datetime(6), token_hash varchar(64), primary key (id)) engine=InnoDB;
create table platform_post (active bit, id integer not null auto_increment, created_at datetime(6), image_url varchar(1000), content LONGTEXT, title varchar(255), type varchar(255), primary key (id)) engine=InnoDB;
create table quiz_attempt_details (is_correct bit, order_index integer, quiz_attempt_detail_id integer not null auto_increment, quiz_question_id integer, quiz_result_id integer, selected_option_id integer, answer_text TEXT, primary key (quiz_attempt_detail_id)) engine=InnoDB;
create table quiz_options (is_correct bit, quiz_option_id integer not null auto_increment, quiz_question_id integer, content TEXT not null, primary key (quiz_option_id)) engine=InnoDB;
create table quiz_questions (order_index integer, quiz_id integer, quiz_question_id integer not null auto_increment, audio_url varchar(1000), content TEXT not null, question_type varchar(255), primary key (quiz_question_id)) engine=InnoDB;
create table quiz_results (correct_answers integer, quiz_results_id integer not null auto_increment, quizes_id integer, score integer, time_spent integer, total_questions integer, user_id integer, wrong_answers integer, submitted_at datetime(6) not null, status varchar(255), primary key (quiz_results_id)) engine=InnoDB;
create table quizes (active boolean default true not null, max_attempts integer, max_pauses integer, pass_score integer, quizes_id integer not null auto_increment, time_limit integer, quiz_type varchar(255), title varchar(255) not null, primary key (quizes_id)) engine=InnoDB;
create table quiz_session (id integer not null auto_increment, quiz_id integer, submitted bit, user_id integer, expires_at datetime(6), started_at datetime(6), primary key (id)) engine=InnoDB;
create table refresh_token (user_id integer not null, expiry_date datetime(6) not null, id bigint not null auto_increment, token varchar(500) not null, primary key (id)) engine=InnoDB;
create table roles (role_id integer not null auto_increment, role_name varchar(255), primary key (role_id)) engine=InnoDB;
create table saved_word (id integer not null auto_increment, mastered bit, user_id integer, back varchar(255), front varchar(255), primary key (id)) engine=InnoDB;
create table sepay_transactions (amount decimal(15,2), order_id integer, id bigint not null, received_at datetime(6), content varchar(2000), account_number varchar(255), gateway varchar(255), order_code varchar(255), reason varchar(255), reference_code varchar(255), status varchar(255), sub_account varchar(255), primary key (id)) engine=InnoDB;
create table shipping_orders (order_id integer, shipping_order_id integer not null auto_increment, phone varchar(15) not null, address TEXT not null, full_name varchar(255) not null, primary key (shipping_order_id)) engine=InnoDB;
create table user_courses (course_id integer not null, user_course_id integer not null auto_increment, user_id integer not null, enrolled_at datetime(6), status varchar(50), primary key (user_course_id)) engine=InnoDB;
create table user_lesson_progress (is_completed bit, last_position integer, lesson_id integer not null, user_id integer not null, user_lesson_progress_id integer not null auto_increment, completed_at datetime(6), last_accessed_at datetime(6), primary key (user_lesson_progress_id)) engine=InnoDB;
create table users (role_id integer not null, status bit, token_version integer default 0 not null, user_id integer not null auto_increment, created_at datetime(6), password varchar(512), avater_url varchar(1000), address varchar(255), email varchar(255), full_name varchar(255), phone varchar(255), username varchar(255), primary key (user_id)) engine=InnoDB;
create table voucher (active bit, discount decimal(15,2), expired_at date, id integer not null auto_increment, max_discount decimal(15,2), max_usage integer, min_order decimal(15,2), used_count integer, code varchar(255), type varchar(255), primary key (id)) engine=InnoDB;
alter table carts add constraint UK64t7ox312pqal3p7fg9o503c2 unique (user_id);
alter table favorite add constraint UK8vb5nkhxrxpd7ntxnh1d7s2we unique (user_id, item_id, item_type);
alter table orders add constraint uk_orders_user_checkout unique (user_id, checkout_key);
alter table orders add constraint UKdhk2umg8ijjkg4njg6891trit unique (order_code);
alter table password_reset add constraint UK4ecuna6e9y4igqvardanvj2kd unique (token_hash);
alter table refresh_token add constraint UKr4k4edos30bx9neoq81mdvwph unique (token);
alter table shipping_orders add constraint UK9nemmy106wav6coahay0rywhl unique (order_id);
alter table users add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);
alter table users add constraint UKr43af9ap4edm43mmtq01oddj6 unique (username);
alter table voucher add constraint UKpvh1lqheshnjoekevvwla03xn unique (code);
alter table book_category_mapping add constraint FK4eis1k6dq4mcxoxhacoqnutob foreign key (category_id) references categories (category_id);
alter table book_category_mapping add constraint FKev0nw8b1vorv4hnd2twi2jk9n foreign key (book_id) references books (book_id);
alter table book_images add constraint FKcnpy06tjmrsjisjf2bqpuvvbl foreign key (book_id) references books (book_id);
alter table cart_items add constraint FKpcttvuq4mxppo8sxggjtn5i2c foreign key (cart_id) references carts (cart_id);
alter table carts add constraint FKb5o626f86h46m4s7ms6ginnop foreign key (user_id) references users (user_id);
alter table chapters add constraint FK6h1m0nrtdwj37570c0sp2tdcs foreign key (course_id) references courses (course_id);
alter table combo_items add constraint FK48h944x1aynyjkgfxa1tu5n32 foreign key (combo_id) references combos (combo_id);
alter table comments add constraint FK37jam8u2nwqw9enhv7nqn52e4 foreign key (lesson_id) references lessons (lesson_id);
alter table comments add constraint FKlri30okf66phtcgbe5pok7cc0 foreign key (parent_id) references comments (comment_id);
alter table comments add constraint FK8omq0tc18jd43bu5tjh6jvraq foreign key (user_id) references users (user_id);
alter table course_contents add constraint FKrijvq02ioctmrw3ihpu1vjrdr foreign key (chapter_id) references chapters (chapter_id);
alter table course_contents add constraint FK6uu6sx29chqt2f33gke1ct353 foreign key (lesson_id) references lessons (lesson_id);
alter table course_contents add constraint FK7ep6mtvewatjiwi9bnmc0ckuj foreign key (quiz_id) references quizes (quizes_id);
alter table flashcards add constraint FKqa6s3qvklb75vyk5uji7n140i foreign key (lesson_id) references lessons (lesson_id);
alter table order_items add constraint FKbioxgbv59vetrxe0ejfubep1w foreign key (order_id) references orders (order_id);
alter table orders add constraint FK32ql8ubntj5uh44ph9659tiih foreign key (user_id) references users (user_id);
alter table quiz_attempt_details add constraint FKala74xhc487rhy7e9acudnjet foreign key (quiz_question_id) references quiz_questions (quiz_question_id);
alter table quiz_attempt_details add constraint FK55flpyil975b6sgmhh3h0ddj foreign key (quiz_result_id) references quiz_results (quiz_results_id);
alter table quiz_attempt_details add constraint FKcg285yeex3eb1pm2gc3s1s18w foreign key (selected_option_id) references quiz_options (quiz_option_id);
alter table quiz_options add constraint FKhpr35shfeo1uxwqnx0kwxd0b0 foreign key (quiz_question_id) references quiz_questions (quiz_question_id);
alter table quiz_questions add constraint FKbuts0vh24fi8nv5rbn999rriv foreign key (quiz_id) references quizes (quizes_id);
alter table quiz_results add constraint FK3hydlxnyfcwptvobu9dg16hx1 foreign key (quizes_id) references quizes (quizes_id);
alter table quiz_results add constraint FKc31xkn83q9v6yf9gh2spkvxrc foreign key (user_id) references users (user_id);
alter table refresh_token add constraint FKjtx87i0jvq2svedphegvdwcuy foreign key (user_id) references users (user_id);
alter table shipping_orders add constraint FKlfb3u79ee02ok91hxcwqp4ji1 foreign key (order_id) references orders (order_id);
alter table user_courses add constraint FKb84hga2qpwc4vv44lmyb8mwux foreign key (course_id) references courses (course_id);
alter table user_courses add constraint FK5i2mwg17kvpk92fy6cdii93da foreign key (user_id) references users (user_id);
alter table user_lesson_progress add constraint FKkm3wlef4wdkrsdwkbly0sid0l foreign key (lesson_id) references lessons (lesson_id);
alter table user_lesson_progress add constraint FK15tdn9l75kjm9imsarb223k9q foreign key (user_id) references users (user_id);
alter table users add constraint FKp56c1712k691lhsyewcssf40f foreign key (role_id) references roles (role_id);

-- JLer DEMO DATA: chỉ hai vai trò ADMIN / USER.
-- Chạy trên schema đã có đầy đủ bảng (bao gồm token_version).
-- Có thể chạy lại: không reset mật khẩu/tồn kho/dữ liệu đã có; chỉ thêm dữ liệu mẫu còn thiếu.
-- Tài khoản admin mới: admin / JLerAdmin2026! (nếu admin đã tồn tại, giữ mật khẩu cũ).
-- Người dùng mẫu: demo_user1, demo_user2 / JLerUser2026!
-- Các đơn/điểm học là DỮ LIỆU MẪU; không phải giao dịch ngân hàng thật.
USE jler_chinese_system;
SET NAMES utf8mb4;
START TRANSACTION;
UPDATE book_images SET image_url='/uploads/jler-media/study-desk.png' WHERE image_url='/uploads/demo-cover-0.svg';
UPDATE courses SET thumbnail_url='/uploads/jler-media/study-desk.png' WHERE thumbnail_url='/uploads/demo-cover-0.svg';
UPDATE platform_post SET image_url='/uploads/jler-media/study-desk.png' WHERE image_url='/uploads/demo-cover-0.svg';
UPDATE book_images SET image_url='/uploads/jler-media/hsk-books.png' WHERE image_url='/uploads/demo-cover-1.svg';
UPDATE courses SET thumbnail_url='/uploads/jler-media/hsk-books.png' WHERE thumbnail_url='/uploads/demo-cover-1.svg';
UPDATE platform_post SET image_url='/uploads/jler-media/hsk-books.png' WHERE image_url='/uploads/demo-cover-1.svg';
UPDATE book_images SET image_url='/uploads/jler-media/conversation.png' WHERE image_url='/uploads/demo-cover-2.svg';
UPDATE courses SET thumbnail_url='/uploads/jler-media/conversation.png' WHERE thumbnail_url='/uploads/demo-cover-2.svg';
UPDATE platform_post SET image_url='/uploads/jler-media/conversation.png' WHERE image_url='/uploads/demo-cover-2.svg';
UPDATE lessons SET video_url='/uploads/jler-media/chao-hoi.mp4',duration=42 WHERE title IN ('Chào hỏi và giới thiệu','[Mẫu] Chào hỏi và giới thiệu') AND (video_url IS NULL OR TRIM(video_url)='');
UPDATE lessons SET video_url='/uploads/jler-media/so-dem.mp4',duration=72 WHERE title IN ('Đếm số và hỏi giá','[Mẫu] Đếm số và hỏi giá') AND (video_url IS NULL OR TRIM(video_url)='');


-- Chuẩn hóa nhãn trước khi tìm dữ liệu đã có để không tạo trùng.
UPDATE books SET title=REPLACE(title, '[Mẫu] ', '') WHERE title LIKE '%[Mẫu] %';
UPDATE books SET description=REPLACE(description, '[Mẫu] ', '') WHERE description LIKE '%[Mẫu] %';
UPDATE courses SET title=REPLACE(title, '[Mẫu] ', '') WHERE title LIKE '%[Mẫu] %';
UPDATE courses SET description=REPLACE(description, '[Mẫu] ', '') WHERE description LIKE '%[Mẫu] %';
UPDATE chapters SET title=REPLACE(title, '[Mẫu] ', '') WHERE title LIKE '%[Mẫu] %';
UPDATE lessons SET title=REPLACE(title, '[Mẫu] ', '') WHERE title LIKE '%[Mẫu] %';
UPDATE quizes SET title=REPLACE(title, '[Mẫu] ', '') WHERE title LIKE '%[Mẫu] %';
UPDATE platform_post SET title=REPLACE(title, '[Mẫu] ', '') WHERE title LIKE '%[Mẫu] %';
UPDATE platform_post SET content=REPLACE(content, '[Mẫu] ', '') WHERE content LIKE '%[Mẫu] %';
UPDATE order_items SET item_title=REPLACE(item_title, '[Mẫu] ', '') WHERE item_title LIKE '%[Mẫu] %';
UPDATE comments SET content=REPLACE(content, '[Mẫu] ', '') WHERE content LIKE '%[Mẫu] %';
UPDATE users SET full_name=REPLACE(full_name,'Người dùng mẫu ','Người dùng ') WHERE username IN ('demo_user1','demo_user2') AND full_name LIKE 'Người dùng mẫu %';
UPDATE shipping_orders SET full_name=REPLACE(full_name,'Người nhận mẫu ','Người nhận ') WHERE full_name IN ('Người nhận mẫu 1','Người nhận mẫu 2');
UPDATE books SET description=REPLACE(description,'Tài liệu mẫu JLer: ','Tài liệu JLer: ') WHERE description LIKE 'Tài liệu mẫu JLer: %';
UPDATE courses SET description=REPLACE(description,'Khóa học mẫu giúp','Khóa học giúp') WHERE description LIKE 'Khóa học mẫu giúp%';


-- Chỉ giữ ADMIN và USER; bảo toàn tài khoản và dữ liệu nghiệp vụ.
INSERT INTO roles (role_name) SELECT 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE UPPER(role_name)='ADMIN');
INSERT INTO roles (role_name) SELECT 'USER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE UPPER(role_name)='USER');
SET @role_admin = (SELECT MIN(role_id) FROM roles WHERE UPPER(role_name)='ADMIN');
SET @role_user = (SELECT MIN(role_id) FROM roles WHERE UPPER(role_name)='USER');
DELETE FROM refresh_token WHERE user_id IN (
 SELECT u.user_id FROM users u JOIN roles r ON r.role_id=u.role_id
 WHERE u.role_id <> CASE WHEN UPPER(r.role_name)='ADMIN' THEN @role_admin ELSE @role_user END
);
UPDATE users SET token_version=COALESCE(token_version,0)+1,
 role_id=CASE WHEN role_id IN (SELECT role_id FROM roles WHERE UPPER(role_name)='ADMIN') THEN @role_admin ELSE @role_user END
WHERE role_id <> CASE WHEN role_id IN (SELECT role_id FROM roles WHERE UPPER(role_name)='ADMIN') THEN @role_admin ELSE @role_user END;
DELETE FROM roles WHERE role_id NOT IN (@role_admin,@role_user);
UPDATE roles SET role_name='ADMIN' WHERE role_id=@role_admin;
UPDATE roles SET role_name='USER' WHERE role_id=@role_user;


-- TÀI KHOẢN MẪU, mật khẩu BCrypt tương thích Spring Security
INSERT INTO users (username, email, full_name, password, role_id, status, token_version, phone, address, created_at)
SELECT 'admin', 'admin@jler.example', 'Quản trị JLer', '$2a$10$nKwWm/6jqtbmq.DuKDDvbOPd690J31bQST9OU2FbNm9VEAToSP5Eu', @role_admin, TRUE, 0, '0900000000', 'Địa chỉ minh họa, Hà Nội', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='admin' OR email='admin@jler.example');
INSERT INTO users (username, email, full_name, password, role_id, status, token_version, phone, address, created_at)
SELECT 'demo_user1', 'demo_user1@jler.example', 'Người dùng 01', '$2a$10$iSX/ujsrQanIplG0qwlE2ObSy0jXLT3/FTiuqvqNW1Rp0hg6C0XfC', @role_user, TRUE, 0, '0900000001', 'Địa chỉ minh họa, Hà Nội', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='demo_user1' OR email='demo_user1@jler.example');
SET @u1 = (SELECT MIN(user_id) FROM users WHERE username='demo_user1');
INSERT INTO users (username, email, full_name, password, role_id, status, token_version, phone, address, created_at)
SELECT 'demo_user2', 'demo_user2@jler.example', 'Người dùng 02', '$2a$10$iSX/ujsrQanIplG0qwlE2ObSy0jXLT3/FTiuqvqNW1Rp0hg6C0XfC', @role_user, TRUE, 0, '0900000002', 'Địa chỉ minh họa, Hà Nội', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='demo_user2' OR email='demo_user2@jler.example');
SET @u2 = (SELECT MIN(user_id) FROM users WHERE username='demo_user2');

-- DANH MỤC VÀ SÁCH
INSERT INTO categories (name_category)
SELECT 'HSK 1'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name_category='HSK 1');
SET @cat1 = (SELECT MIN(category_id) FROM categories WHERE name_category='HSK 1');
INSERT INTO categories (name_category)
SELECT 'HSK 2'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name_category='HSK 2');
SET @cat2 = (SELECT MIN(category_id) FROM categories WHERE name_category='HSK 2');
INSERT INTO categories (name_category)
SELECT 'HSK 3'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name_category='HSK 3');
SET @cat3 = (SELECT MIN(category_id) FROM categories WHERE name_category='HSK 3');
INSERT INTO categories (name_category)
SELECT 'Giao tiếp'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name_category='Giao tiếp');
SET @cat4 = (SELECT MIN(category_id) FROM categories WHERE name_category='Giao tiếp');
INSERT INTO categories (name_category)
SELECT 'Hán tự'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name_category='Hán tự');
SET @cat5 = (SELECT MIN(category_id) FROM categories WHERE name_category='Hán tự');
INSERT INTO books (title, description, price, discount_price, stock)
SELECT 'Giáo trình HSK 1', '[JLer Demo] Giáo trình HSK 1. Giáo trình minh họa để thử tìm kiếm, giỏ hàng và đặt sách. Thay thông tin xuất bản thật trước khi bán.', 120000, NULL, 49
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title='Giáo trình HSK 1');
SET @b1 = (SELECT MIN(book_id) FROM books WHERE title='Giáo trình HSK 1');
INSERT INTO book_category_mapping (book_id, category_id)
SELECT @b1, @cat1
WHERE NOT EXISTS (SELECT 1 FROM book_category_mapping WHERE book_id=@b1 AND category_id=@cat1);
INSERT INTO book_images (book_id, image_url, is_thumbnail, created_at)
SELECT @b1, '/uploads/jler-media/study-desk.png', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM book_images WHERE book_id=@b1 AND image_url='/uploads/jler-media/study-desk.png');
INSERT INTO books (title, description, price, discount_price, stock)
SELECT 'Bài tập HSK 1', '[JLer Demo] Bài tập HSK 1. Giáo trình minh họa để thử tìm kiếm, giỏ hàng và đặt sách. Thay thông tin xuất bản thật trước khi bán.', 140000, NULL, 51
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title='Bài tập HSK 1');
SET @b2 = (SELECT MIN(book_id) FROM books WHERE title='Bài tập HSK 1');
INSERT INTO book_category_mapping (book_id, category_id)
SELECT @b2, @cat1
WHERE NOT EXISTS (SELECT 1 FROM book_category_mapping WHERE book_id=@b2 AND category_id=@cat1);
INSERT INTO book_images (book_id, image_url, is_thumbnail, created_at)
SELECT @b2, '/uploads/jler-media/hsk-books.png', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM book_images WHERE book_id=@b2 AND image_url='/uploads/jler-media/hsk-books.png');
INSERT INTO books (title, description, price, discount_price, stock)
SELECT 'Giáo trình HSK 2', '[JLer Demo] Giáo trình HSK 2. Giáo trình minh họa để thử tìm kiếm, giỏ hàng và đặt sách. Thay thông tin xuất bản thật trước khi bán.', 160000, NULL, 52
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title='Giáo trình HSK 2');
SET @b3 = (SELECT MIN(book_id) FROM books WHERE title='Giáo trình HSK 2');
INSERT INTO book_category_mapping (book_id, category_id)
SELECT @b3, @cat2
WHERE NOT EXISTS (SELECT 1 FROM book_category_mapping WHERE book_id=@b3 AND category_id=@cat2);
INSERT INTO book_images (book_id, image_url, is_thumbnail, created_at)
SELECT @b3, '/uploads/jler-media/conversation.png', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM book_images WHERE book_id=@b3 AND image_url='/uploads/jler-media/conversation.png');
INSERT INTO books (title, description, price, discount_price, stock)
SELECT 'Từ vựng HSK 2', '[JLer Demo] Từ vựng HSK 2. Giáo trình minh họa để thử tìm kiếm, giỏ hàng và đặt sách. Thay thông tin xuất bản thật trước khi bán.', 180000, NULL, 53
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title='Từ vựng HSK 2');
SET @b4 = (SELECT MIN(book_id) FROM books WHERE title='Từ vựng HSK 2');
INSERT INTO book_category_mapping (book_id, category_id)
SELECT @b4, @cat2
WHERE NOT EXISTS (SELECT 1 FROM book_category_mapping WHERE book_id=@b4 AND category_id=@cat2);
INSERT INTO book_images (book_id, image_url, is_thumbnail, created_at)
SELECT @b4, '/uploads/jler-media/study-desk.png', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM book_images WHERE book_id=@b4 AND image_url='/uploads/jler-media/study-desk.png');
INSERT INTO books (title, description, price, discount_price, stock)
SELECT 'Bộ đề HSK 3', '[JLer Demo] Bộ đề HSK 3. Giáo trình minh họa để thử tìm kiếm, giỏ hàng và đặt sách. Thay thông tin xuất bản thật trước khi bán.', 200000, NULL, 55
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title='Bộ đề HSK 3');
SET @b5 = (SELECT MIN(book_id) FROM books WHERE title='Bộ đề HSK 3');
INSERT INTO book_category_mapping (book_id, category_id)
SELECT @b5, @cat3
WHERE NOT EXISTS (SELECT 1 FROM book_category_mapping WHERE book_id=@b5 AND category_id=@cat3);
INSERT INTO book_images (book_id, image_url, is_thumbnail, created_at)
SELECT @b5, '/uploads/jler-media/hsk-books.png', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM book_images WHERE book_id=@b5 AND image_url='/uploads/jler-media/hsk-books.png');
INSERT INTO books (title, description, price, discount_price, stock)
SELECT 'Ngữ pháp HSK 3', '[JLer Demo] Ngữ pháp HSK 3. Giáo trình minh họa để thử tìm kiếm, giỏ hàng và đặt sách. Thay thông tin xuất bản thật trước khi bán.', 220000, NULL, 56
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title='Ngữ pháp HSK 3');
SET @b6 = (SELECT MIN(book_id) FROM books WHERE title='Ngữ pháp HSK 3');
INSERT INTO book_category_mapping (book_id, category_id)
SELECT @b6, @cat3
WHERE NOT EXISTS (SELECT 1 FROM book_category_mapping WHERE book_id=@b6 AND category_id=@cat3);
INSERT INTO book_images (book_id, image_url, is_thumbnail, created_at)
SELECT @b6, '/uploads/jler-media/conversation.png', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM book_images WHERE book_id=@b6 AND image_url='/uploads/jler-media/conversation.png');
INSERT INTO books (title, description, price, discount_price, stock)
SELECT 'Giao tiếp mỗi ngày', '[JLer Demo] Giao tiếp mỗi ngày. Giáo trình minh họa để thử tìm kiếm, giỏ hàng và đặt sách. Thay thông tin xuất bản thật trước khi bán.', 240000, NULL, 57
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title='Giao tiếp mỗi ngày');
SET @b7 = (SELECT MIN(book_id) FROM books WHERE title='Giao tiếp mỗi ngày');
INSERT INTO book_category_mapping (book_id, category_id)
SELECT @b7, @cat4
WHERE NOT EXISTS (SELECT 1 FROM book_category_mapping WHERE book_id=@b7 AND category_id=@cat4);
INSERT INTO book_images (book_id, image_url, is_thumbnail, created_at)
SELECT @b7, '/uploads/jler-media/study-desk.png', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM book_images WHERE book_id=@b7 AND image_url='/uploads/jler-media/study-desk.png');
INSERT INTO books (title, description, price, discount_price, stock)
SELECT 'Tiếng Trung du lịch', '[JLer Demo] Tiếng Trung du lịch. Giáo trình minh họa để thử tìm kiếm, giỏ hàng và đặt sách. Thay thông tin xuất bản thật trước khi bán.', 260000, NULL, 58
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title='Tiếng Trung du lịch');
SET @b8 = (SELECT MIN(book_id) FROM books WHERE title='Tiếng Trung du lịch');
INSERT INTO book_category_mapping (book_id, category_id)
SELECT @b8, @cat4
WHERE NOT EXISTS (SELECT 1 FROM book_category_mapping WHERE book_id=@b8 AND category_id=@cat4);
INSERT INTO book_images (book_id, image_url, is_thumbnail, created_at)
SELECT @b8, '/uploads/jler-media/hsk-books.png', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM book_images WHERE book_id=@b8 AND image_url='/uploads/jler-media/hsk-books.png');
INSERT INTO books (title, description, price, discount_price, stock)
SELECT 'Tập viết Hán tự', '[JLer Demo] Tập viết Hán tự. Giáo trình minh họa để thử tìm kiếm, giỏ hàng và đặt sách. Thay thông tin xuất bản thật trước khi bán.', 280000, NULL, 59
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title='Tập viết Hán tự');
SET @b9 = (SELECT MIN(book_id) FROM books WHERE title='Tập viết Hán tự');
INSERT INTO book_category_mapping (book_id, category_id)
SELECT @b9, @cat5
WHERE NOT EXISTS (SELECT 1 FROM book_category_mapping WHERE book_id=@b9 AND category_id=@cat5);
INSERT INTO book_images (book_id, image_url, is_thumbnail, created_at)
SELECT @b9, '/uploads/jler-media/conversation.png', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM book_images WHERE book_id=@b9 AND image_url='/uploads/jler-media/conversation.png');
INSERT INTO books (title, description, price, discount_price, stock)
SELECT '214 bộ thủ cơ bản', '[JLer Demo] 214 bộ thủ cơ bản. Giáo trình minh họa để thử tìm kiếm, giỏ hàng và đặt sách. Thay thông tin xuất bản thật trước khi bán.', 300000, NULL, 60
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title='214 bộ thủ cơ bản');
SET @b10 = (SELECT MIN(book_id) FROM books WHERE title='214 bộ thủ cơ bản');
INSERT INTO book_category_mapping (book_id, category_id)
SELECT @b10, @cat5
WHERE NOT EXISTS (SELECT 1 FROM book_category_mapping WHERE book_id=@b10 AND category_id=@cat5);
INSERT INTO book_images (book_id, image_url, is_thumbnail, created_at)
SELECT @b10, '/uploads/jler-media/study-desk.png', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM book_images WHERE book_id=@b10 AND image_url='/uploads/jler-media/study-desk.png');
INSERT INTO books (title, description, price, discount_price, stock)
SELECT 'Từ vựng qua hình ảnh', '[JLer Demo] Từ vựng qua hình ảnh. Giáo trình minh họa để thử tìm kiếm, giỏ hàng và đặt sách. Thay thông tin xuất bản thật trước khi bán.', 320000, NULL, 61
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title='Từ vựng qua hình ảnh');
SET @b11 = (SELECT MIN(book_id) FROM books WHERE title='Từ vựng qua hình ảnh');
INSERT INTO book_category_mapping (book_id, category_id)
SELECT @b11, @cat1
WHERE NOT EXISTS (SELECT 1 FROM book_category_mapping WHERE book_id=@b11 AND category_id=@cat1);
INSERT INTO book_images (book_id, image_url, is_thumbnail, created_at)
SELECT @b11, '/uploads/jler-media/hsk-books.png', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM book_images WHERE book_id=@b11 AND image_url='/uploads/jler-media/hsk-books.png');
INSERT INTO books (title, description, price, discount_price, stock)
SELECT 'Luyện nghe sơ cấp', '[JLer Demo] Luyện nghe sơ cấp. Giáo trình minh họa để thử tìm kiếm, giỏ hàng và đặt sách. Thay thông tin xuất bản thật trước khi bán.', 340000, NULL, 62
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title='Luyện nghe sơ cấp');
SET @b12 = (SELECT MIN(book_id) FROM books WHERE title='Luyện nghe sơ cấp');
INSERT INTO book_category_mapping (book_id, category_id)
SELECT @b12, @cat2
WHERE NOT EXISTS (SELECT 1 FROM book_category_mapping WHERE book_id=@b12 AND category_id=@cat2);
INSERT INTO book_images (book_id, image_url, is_thumbnail, created_at)
SELECT @b12, '/uploads/jler-media/conversation.png', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM book_images WHERE book_id=@b12 AND image_url='/uploads/jler-media/conversation.png');

-- KHÓA HỌC / CHƯƠNG / BÀI HỌC / FLASHCARD
INSERT INTO courses (title, description, price, discount_price, thumbnail_url, course_type, status, total_lesson, total_duration)
SELECT 'HSK 1 – Tiếng Trung từ đầu', '[JLer Demo] Khóa học văn bản và flashcard minh họa. Không kèm video thương mại. Có thể học thử bài đầu và mua để mở các bài tiếp theo.', 199000, NULL, '/uploads/jler-media/study-desk.png', 'HSK', 'ACTIVE', 4, 3000
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title='HSK 1 – Tiếng Trung từ đầu');
SET @c1 = (SELECT MIN(course_id) FROM courses WHERE title='HSK 1 – Tiếng Trung từ đầu');
INSERT INTO chapters (course_id, title, order_index, is_active)
SELECT @c1, 'Làm quen ngôn ngữ', 1, TRUE
WHERE NOT EXISTS (SELECT 1 FROM chapters WHERE course_id=@c1 AND title='Làm quen ngôn ngữ');
SET @ch1_1 = (SELECT MIN(chapter_id) FROM chapters WHERE course_id=@c1 AND title='Làm quen ngôn ngữ');
SET @l1 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch1_1 AND x.content_type='LESSON' AND x.order_index=1);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Chào hỏi và giới thiệu','你好！Nǐ hǎo! — Xin chào.
我叫安。Wǒ jiào Ān. — Tôi tên là An.
谢谢！Xièxie! — Cảm ơn.
再见！Zàijiàn! — Tạm biệt.
Thực hành: tự giới thiệu tên của bạn.',600,TRUE WHERE @l1 IS NULL;
SET @l1 = COALESCE(@l1,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch1_1, @l1, 'LESSON', 1, TRUE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch1_1 AND lesson_id=@l1);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l1, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l1 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l1, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l1 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l1, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l1 AND front_text='再见 · zàijiàn');
SET @l2 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch1_1 AND x.content_type='LESSON' AND x.order_index=2);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Đếm số và hỏi giá','一 yī — một; 二 èr — hai; 三 sān — ba.
多少钱？Duōshao qián? — Bao nhiêu tiền?
十块钱。Shí kuài qián. — Mười tệ.',900,FALSE WHERE @l2 IS NULL;
SET @l2 = COALESCE(@l2,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch1_1, @l2, 'LESSON', 2, FALSE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch1_1 AND lesson_id=@l2);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l2, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l2 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l2, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l2 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l2, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l2 AND front_text='再见 · zàijiàn');
INSERT INTO chapters (course_id, title, order_index, is_active)
SELECT @c1, 'Thực hành mỗi ngày', 2, TRUE
WHERE NOT EXISTS (SELECT 1 FROM chapters WHERE course_id=@c1 AND title='Thực hành mỗi ngày');
SET @ch1_2 = (SELECT MIN(chapter_id) FROM chapters WHERE course_id=@c1 AND title='Thực hành mỗi ngày');
SET @l3 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch1_2 AND x.content_type='LESSON' AND x.order_index=1);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Gia đình và bạn bè','这是我的朋友。Zhè shì wǒ de péngyou. — Đây là bạn của tôi.
妈妈 māma — mẹ.
爸爸 bàba — bố.
Thực hành: giới thiệu một người thân.',600,FALSE WHERE @l3 IS NULL;
SET @l3 = COALESCE(@l3,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch1_2, @l3, 'LESSON', 1, FALSE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch1_2 AND lesson_id=@l3);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l3, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l3 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l3, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l3 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l3, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l3 AND front_text='再见 · zàijiàn');
SET @l4 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch1_2 AND x.content_type='LESSON' AND x.order_index=2);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Thời gian và thói quen','现在几点？Xiànzài jǐ diǎn? — Bây giờ là mấy giờ?
我每天学习中文。Wǒ měitiān xuéxí Zhōngwén. — Tôi học tiếng Trung mỗi ngày.',900,FALSE WHERE @l4 IS NULL;
SET @l4 = COALESCE(@l4,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch1_2, @l4, 'LESSON', 2, FALSE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch1_2 AND lesson_id=@l4);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l4, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l4 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l4, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l4 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l4, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l4 AND front_text='再见 · zàijiàn');
INSERT INTO courses (title, description, price, discount_price, thumbnail_url, course_type, status, total_lesson, total_duration)
SELECT 'HSK 2 – Vững nền tảng', '[JLer Demo] Khóa học văn bản và flashcard minh họa. Không kèm video thương mại. Có thể học thử bài đầu và mua để mở các bài tiếp theo.', 398000, NULL, '/uploads/jler-media/hsk-books.png', 'HSK', 'ACTIVE', 4, 3000
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title='HSK 2 – Vững nền tảng');
SET @c2 = (SELECT MIN(course_id) FROM courses WHERE title='HSK 2 – Vững nền tảng');
INSERT INTO chapters (course_id, title, order_index, is_active)
SELECT @c2, 'Làm quen ngôn ngữ', 1, TRUE
WHERE NOT EXISTS (SELECT 1 FROM chapters WHERE course_id=@c2 AND title='Làm quen ngôn ngữ');
SET @ch2_1 = (SELECT MIN(chapter_id) FROM chapters WHERE course_id=@c2 AND title='Làm quen ngôn ngữ');
SET @l5 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch2_1 AND x.content_type='LESSON' AND x.order_index=1);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Chào hỏi và giới thiệu','你好！Nǐ hǎo! — Xin chào.
我叫安。Wǒ jiào Ān. — Tôi tên là An.
谢谢！Xièxie! — Cảm ơn.
再见！Zàijiàn! — Tạm biệt.
Thực hành: tự giới thiệu tên của bạn.',600,TRUE WHERE @l5 IS NULL;
SET @l5 = COALESCE(@l5,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch2_1, @l5, 'LESSON', 1, TRUE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch2_1 AND lesson_id=@l5);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l5, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l5 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l5, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l5 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l5, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l5 AND front_text='再见 · zàijiàn');
SET @l6 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch2_1 AND x.content_type='LESSON' AND x.order_index=2);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Đếm số và hỏi giá','一 yī — một; 二 èr — hai; 三 sān — ba.
多少钱？Duōshao qián? — Bao nhiêu tiền?
十块钱。Shí kuài qián. — Mười tệ.',900,FALSE WHERE @l6 IS NULL;
SET @l6 = COALESCE(@l6,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch2_1, @l6, 'LESSON', 2, FALSE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch2_1 AND lesson_id=@l6);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l6, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l6 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l6, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l6 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l6, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l6 AND front_text='再见 · zàijiàn');
INSERT INTO chapters (course_id, title, order_index, is_active)
SELECT @c2, 'Thực hành mỗi ngày', 2, TRUE
WHERE NOT EXISTS (SELECT 1 FROM chapters WHERE course_id=@c2 AND title='Thực hành mỗi ngày');
SET @ch2_2 = (SELECT MIN(chapter_id) FROM chapters WHERE course_id=@c2 AND title='Thực hành mỗi ngày');
SET @l7 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch2_2 AND x.content_type='LESSON' AND x.order_index=1);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Gia đình và bạn bè','这是我的朋友。Zhè shì wǒ de péngyou. — Đây là bạn của tôi.
妈妈 māma — mẹ.
爸爸 bàba — bố.
Thực hành: giới thiệu một người thân.',600,FALSE WHERE @l7 IS NULL;
SET @l7 = COALESCE(@l7,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch2_2, @l7, 'LESSON', 1, FALSE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch2_2 AND lesson_id=@l7);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l7, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l7 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l7, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l7 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l7, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l7 AND front_text='再见 · zàijiàn');
SET @l8 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch2_2 AND x.content_type='LESSON' AND x.order_index=2);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Thời gian và thói quen','现在几点？Xiànzài jǐ diǎn? — Bây giờ là mấy giờ?
我每天学习中文。Wǒ měitiān xuéxí Zhōngwén. — Tôi học tiếng Trung mỗi ngày.',900,FALSE WHERE @l8 IS NULL;
SET @l8 = COALESCE(@l8,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch2_2, @l8, 'LESSON', 2, FALSE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch2_2 AND lesson_id=@l8);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l8, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l8 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l8, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l8 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l8, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l8 AND front_text='再见 · zàijiàn');
INSERT INTO courses (title, description, price, discount_price, thumbnail_url, course_type, status, total_lesson, total_duration)
SELECT 'Giao tiếp tiếng Trung', '[JLer Demo] Khóa học văn bản và flashcard minh họa. Không kèm video thương mại. Có thể học thử bài đầu và mua để mở các bài tiếp theo.', 597000, NULL, '/uploads/jler-media/conversation.png', 'Giao tiếp', 'ACTIVE', 4, 3000
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title='Giao tiếp tiếng Trung');
SET @c3 = (SELECT MIN(course_id) FROM courses WHERE title='Giao tiếp tiếng Trung');
INSERT INTO chapters (course_id, title, order_index, is_active)
SELECT @c3, 'Làm quen ngôn ngữ', 1, TRUE
WHERE NOT EXISTS (SELECT 1 FROM chapters WHERE course_id=@c3 AND title='Làm quen ngôn ngữ');
SET @ch3_1 = (SELECT MIN(chapter_id) FROM chapters WHERE course_id=@c3 AND title='Làm quen ngôn ngữ');
SET @l9 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch3_1 AND x.content_type='LESSON' AND x.order_index=1);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Chào hỏi và giới thiệu','你好！Nǐ hǎo! — Xin chào.
我叫安。Wǒ jiào Ān. — Tôi tên là An.
谢谢！Xièxie! — Cảm ơn.
再见！Zàijiàn! — Tạm biệt.
Thực hành: tự giới thiệu tên của bạn.',600,TRUE WHERE @l9 IS NULL;
SET @l9 = COALESCE(@l9,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch3_1, @l9, 'LESSON', 1, TRUE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch3_1 AND lesson_id=@l9);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l9, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l9 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l9, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l9 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l9, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l9 AND front_text='再见 · zàijiàn');
SET @l10 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch3_1 AND x.content_type='LESSON' AND x.order_index=2);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Đếm số và hỏi giá','一 yī — một; 二 èr — hai; 三 sān — ba.
多少钱？Duōshao qián? — Bao nhiêu tiền?
十块钱。Shí kuài qián. — Mười tệ.',900,FALSE WHERE @l10 IS NULL;
SET @l10 = COALESCE(@l10,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch3_1, @l10, 'LESSON', 2, FALSE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch3_1 AND lesson_id=@l10);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l10, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l10 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l10, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l10 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l10, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l10 AND front_text='再见 · zàijiàn');
INSERT INTO chapters (course_id, title, order_index, is_active)
SELECT @c3, 'Thực hành mỗi ngày', 2, TRUE
WHERE NOT EXISTS (SELECT 1 FROM chapters WHERE course_id=@c3 AND title='Thực hành mỗi ngày');
SET @ch3_2 = (SELECT MIN(chapter_id) FROM chapters WHERE course_id=@c3 AND title='Thực hành mỗi ngày');
SET @l11 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch3_2 AND x.content_type='LESSON' AND x.order_index=1);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Gia đình và bạn bè','这是我的朋友。Zhè shì wǒ de péngyou. — Đây là bạn của tôi.
妈妈 māma — mẹ.
爸爸 bàba — bố.
Thực hành: giới thiệu một người thân.',600,FALSE WHERE @l11 IS NULL;
SET @l11 = COALESCE(@l11,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch3_2, @l11, 'LESSON', 1, FALSE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch3_2 AND lesson_id=@l11);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l11, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l11 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l11, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l11 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l11, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l11 AND front_text='再见 · zàijiàn');
SET @l12 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch3_2 AND x.content_type='LESSON' AND x.order_index=2);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Thời gian và thói quen','现在几点？Xiànzài jǐ diǎn? — Bây giờ là mấy giờ?
我每天学习中文。Wǒ měitiān xuéxí Zhōngwén. — Tôi học tiếng Trung mỗi ngày.',900,FALSE WHERE @l12 IS NULL;
SET @l12 = COALESCE(@l12,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch3_2, @l12, 'LESSON', 2, FALSE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch3_2 AND lesson_id=@l12);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l12, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l12 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l12, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l12 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l12, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l12 AND front_text='再见 · zàijiàn');
INSERT INTO courses (title, description, price, discount_price, thumbnail_url, course_type, status, total_lesson, total_duration)
SELECT 'HSK 3 – Luyện thi', '[JLer Demo] Khóa học văn bản và flashcard minh họa. Không kèm video thương mại. Có thể học thử bài đầu và mua để mở các bài tiếp theo.', 796000, NULL, '/uploads/jler-media/study-desk.png', 'HSK', 'ACTIVE', 4, 3000
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title='HSK 3 – Luyện thi');
SET @c4 = (SELECT MIN(course_id) FROM courses WHERE title='HSK 3 – Luyện thi');
INSERT INTO chapters (course_id, title, order_index, is_active)
SELECT @c4, 'Làm quen ngôn ngữ', 1, TRUE
WHERE NOT EXISTS (SELECT 1 FROM chapters WHERE course_id=@c4 AND title='Làm quen ngôn ngữ');
SET @ch4_1 = (SELECT MIN(chapter_id) FROM chapters WHERE course_id=@c4 AND title='Làm quen ngôn ngữ');
SET @l13 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch4_1 AND x.content_type='LESSON' AND x.order_index=1);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Chào hỏi và giới thiệu','你好！Nǐ hǎo! — Xin chào.
我叫安。Wǒ jiào Ān. — Tôi tên là An.
谢谢！Xièxie! — Cảm ơn.
再见！Zàijiàn! — Tạm biệt.
Thực hành: tự giới thiệu tên của bạn.',600,TRUE WHERE @l13 IS NULL;
SET @l13 = COALESCE(@l13,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch4_1, @l13, 'LESSON', 1, TRUE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch4_1 AND lesson_id=@l13);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l13, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l13 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l13, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l13 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l13, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l13 AND front_text='再见 · zàijiàn');
SET @l14 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch4_1 AND x.content_type='LESSON' AND x.order_index=2);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Đếm số và hỏi giá','一 yī — một; 二 èr — hai; 三 sān — ba.
多少钱？Duōshao qián? — Bao nhiêu tiền?
十块钱。Shí kuài qián. — Mười tệ.',900,FALSE WHERE @l14 IS NULL;
SET @l14 = COALESCE(@l14,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch4_1, @l14, 'LESSON', 2, FALSE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch4_1 AND lesson_id=@l14);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l14, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l14 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l14, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l14 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l14, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l14 AND front_text='再见 · zàijiàn');
INSERT INTO chapters (course_id, title, order_index, is_active)
SELECT @c4, 'Thực hành mỗi ngày', 2, TRUE
WHERE NOT EXISTS (SELECT 1 FROM chapters WHERE course_id=@c4 AND title='Thực hành mỗi ngày');
SET @ch4_2 = (SELECT MIN(chapter_id) FROM chapters WHERE course_id=@c4 AND title='Thực hành mỗi ngày');
SET @l15 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch4_2 AND x.content_type='LESSON' AND x.order_index=1);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Gia đình và bạn bè','这是我的朋友。Zhè shì wǒ de péngyou. — Đây là bạn của tôi.
妈妈 māma — mẹ.
爸爸 bàba — bố.
Thực hành: giới thiệu một người thân.',600,FALSE WHERE @l15 IS NULL;
SET @l15 = COALESCE(@l15,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch4_2, @l15, 'LESSON', 1, FALSE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch4_2 AND lesson_id=@l15);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l15, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l15 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l15, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l15 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l15, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l15 AND front_text='再见 · zàijiàn');
SET @l16 = (SELECT MIN(l.lesson_id) FROM lessons l JOIN course_contents x ON x.lesson_id=l.lesson_id WHERE x.chapter_id=@ch4_2 AND x.content_type='LESSON' AND x.order_index=2);
INSERT INTO lessons (title,content,duration,is_free)
SELECT 'Thời gian và thói quen','现在几点？Xiànzài jǐ diǎn? — Bây giờ là mấy giờ?
我每天学习中文。Wǒ měitiān xuéxí Zhōngwén. — Tôi học tiếng Trung mỗi ngày.',900,FALSE WHERE @l16 IS NULL;
SET @l16 = COALESCE(@l16,LAST_INSERT_ID());
INSERT INTO course_contents (chapter_id, lesson_id, content_type, order_index, is_preview)
SELECT @ch4_2, @l16, 'LESSON', 2, FALSE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch4_2 AND lesson_id=@l16);
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l16, '你好 · nǐ hǎo', 'Xin chào'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l16 AND front_text='你好 · nǐ hǎo');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l16, '谢谢 · xièxie', 'Cảm ơn'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l16 AND front_text='谢谢 · xièxie');
INSERT INTO flashcards (lesson_id, front_text, back_text)
SELECT @l16, '再见 · zàijiàn', 'Tạm biệt'
WHERE NOT EXISTS (SELECT 1 FROM flashcards WHERE lesson_id=@l16 AND front_text='再见 · zàijiàn');

-- ĐỀ THI VÀ ĐÁP ÁN
INSERT INTO quizes (title, quiz_type, pass_score, time_limit, max_attempts, max_pauses)
SELECT 'Luyện tập chào hỏi', 'PRACTICE', 60, 10, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM quizes WHERE title='Luyện tập chào hỏi');
SET @q1 = (SELECT MIN(quizes_id) FROM quizes WHERE title='Luyện tập chào hỏi');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q1, 0, 'SINGLE_CHOICE', '你好 có nghĩa là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q1 AND order_index=0);
SET @qq1_0 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q1 AND order_index=0);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_0, 'Xin chào', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_0 AND content='Xin chào');
SET @op1_0_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_0 AND content='Xin chào');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_0, 'Cảm ơn', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_0 AND content='Cảm ơn');
SET @op1_0_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_0 AND content='Cảm ơn');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_0, 'Tạm biệt', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_0 AND content='Tạm biệt');
SET @op1_0_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_0 AND content='Tạm biệt');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_0, 'Xin lỗi', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_0 AND content='Xin lỗi');
SET @op1_0_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_0 AND content='Xin lỗi');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q1, 1, 'SINGLE_CHOICE', '谢谢 đọc như thế nào?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q1 AND order_index=1);
SET @qq1_1 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q1 AND order_index=1);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_1, 'Zàijiàn', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_1 AND content='Zàijiàn');
SET @op1_1_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_1 AND content='Zàijiàn');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_1, 'Xièxie', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_1 AND content='Xièxie');
SET @op1_1_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_1 AND content='Xièxie');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_1, 'Nǐ hǎo', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_1 AND content='Nǐ hǎo');
SET @op1_1_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_1 AND content='Nǐ hǎo');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_1, 'Duìbuqǐ', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_1 AND content='Duìbuqǐ');
SET @op1_1_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_1 AND content='Duìbuqǐ');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q1, 2, 'SINGLE_CHOICE', 'Tạm biệt trong tiếng Trung là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q1 AND order_index=2);
SET @qq1_2 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q1 AND order_index=2);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_2, '你好', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_2 AND content='你好');
SET @op1_2_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_2 AND content='你好');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_2, '谢谢', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_2 AND content='谢谢');
SET @op1_2_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_2 AND content='谢谢');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_2, '再见', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_2 AND content='再见');
SET @op1_2_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_2 AND content='再见');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_2, '我', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_2 AND content='我');
SET @op1_2_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_2 AND content='我');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q1, 3, 'SINGLE_CHOICE', '我 có nghĩa là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q1 AND order_index=3);
SET @qq1_3 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q1 AND order_index=3);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_3, 'Bạn', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_3 AND content='Bạn');
SET @op1_3_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_3 AND content='Bạn');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_3, 'Anh ấy', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_3 AND content='Anh ấy');
SET @op1_3_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_3 AND content='Anh ấy');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_3, 'Cô ấy', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_3 AND content='Cô ấy');
SET @op1_3_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_3 AND content='Cô ấy');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_3, 'Tôi', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_3 AND content='Tôi');
SET @op1_3_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_3 AND content='Tôi');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q1, 4, 'SINGLE_CHOICE', 'Số một trong tiếng Trung là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q1 AND order_index=4);
SET @qq1_4 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q1 AND order_index=4);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_4, '一', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_4 AND content='一');
SET @op1_4_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_4 AND content='一');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_4, '二', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_4 AND content='二');
SET @op1_4_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_4 AND content='二');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_4, '三', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_4 AND content='三');
SET @op1_4_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_4 AND content='三');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq1_4, '十', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq1_4 AND content='十');
SET @op1_4_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq1_4 AND content='十');
INSERT INTO quizes (title, quiz_type, pass_score, time_limit, max_attempts, max_pauses)
SELECT 'Ôn tập từ vựng sơ cấp', 'PRACTICE', 60, 10, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM quizes WHERE title='Ôn tập từ vựng sơ cấp');
SET @q2 = (SELECT MIN(quizes_id) FROM quizes WHERE title='Ôn tập từ vựng sơ cấp');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q2, 0, 'SINGLE_CHOICE', '你好 có nghĩa là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q2 AND order_index=0);
SET @qq2_0 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q2 AND order_index=0);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_0, 'Xin chào', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_0 AND content='Xin chào');
SET @op2_0_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_0 AND content='Xin chào');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_0, 'Cảm ơn', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_0 AND content='Cảm ơn');
SET @op2_0_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_0 AND content='Cảm ơn');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_0, 'Tạm biệt', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_0 AND content='Tạm biệt');
SET @op2_0_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_0 AND content='Tạm biệt');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_0, 'Xin lỗi', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_0 AND content='Xin lỗi');
SET @op2_0_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_0 AND content='Xin lỗi');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q2, 1, 'SINGLE_CHOICE', '谢谢 đọc như thế nào?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q2 AND order_index=1);
SET @qq2_1 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q2 AND order_index=1);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_1, 'Zàijiàn', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_1 AND content='Zàijiàn');
SET @op2_1_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_1 AND content='Zàijiàn');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_1, 'Xièxie', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_1 AND content='Xièxie');
SET @op2_1_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_1 AND content='Xièxie');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_1, 'Nǐ hǎo', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_1 AND content='Nǐ hǎo');
SET @op2_1_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_1 AND content='Nǐ hǎo');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_1, 'Duìbuqǐ', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_1 AND content='Duìbuqǐ');
SET @op2_1_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_1 AND content='Duìbuqǐ');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q2, 2, 'SINGLE_CHOICE', 'Tạm biệt trong tiếng Trung là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q2 AND order_index=2);
SET @qq2_2 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q2 AND order_index=2);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_2, '你好', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_2 AND content='你好');
SET @op2_2_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_2 AND content='你好');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_2, '谢谢', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_2 AND content='谢谢');
SET @op2_2_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_2 AND content='谢谢');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_2, '再见', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_2 AND content='再见');
SET @op2_2_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_2 AND content='再见');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_2, '我', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_2 AND content='我');
SET @op2_2_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_2 AND content='我');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q2, 3, 'SINGLE_CHOICE', '我 có nghĩa là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q2 AND order_index=3);
SET @qq2_3 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q2 AND order_index=3);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_3, 'Bạn', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_3 AND content='Bạn');
SET @op2_3_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_3 AND content='Bạn');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_3, 'Anh ấy', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_3 AND content='Anh ấy');
SET @op2_3_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_3 AND content='Anh ấy');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_3, 'Cô ấy', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_3 AND content='Cô ấy');
SET @op2_3_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_3 AND content='Cô ấy');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_3, 'Tôi', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_3 AND content='Tôi');
SET @op2_3_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_3 AND content='Tôi');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q2, 4, 'SINGLE_CHOICE', 'Số một trong tiếng Trung là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q2 AND order_index=4);
SET @qq2_4 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q2 AND order_index=4);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_4, '一', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_4 AND content='一');
SET @op2_4_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_4 AND content='一');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_4, '二', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_4 AND content='二');
SET @op2_4_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_4 AND content='二');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_4, '三', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_4 AND content='三');
SET @op2_4_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_4 AND content='三');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq2_4, '十', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq2_4 AND content='十');
SET @op2_4_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq2_4 AND content='十');
INSERT INTO quizes (title, quiz_type, pass_score, time_limit, max_attempts, max_pauses)
SELECT 'Thử sức HSK', 'PRACTICE', 60, 10, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM quizes WHERE title='Thử sức HSK');
SET @q3 = (SELECT MIN(quizes_id) FROM quizes WHERE title='Thử sức HSK');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q3, 0, 'SINGLE_CHOICE', '你好 có nghĩa là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q3 AND order_index=0);
SET @qq3_0 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q3 AND order_index=0);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_0, 'Xin chào', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_0 AND content='Xin chào');
SET @op3_0_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_0 AND content='Xin chào');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_0, 'Cảm ơn', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_0 AND content='Cảm ơn');
SET @op3_0_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_0 AND content='Cảm ơn');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_0, 'Tạm biệt', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_0 AND content='Tạm biệt');
SET @op3_0_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_0 AND content='Tạm biệt');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_0, 'Xin lỗi', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_0 AND content='Xin lỗi');
SET @op3_0_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_0 AND content='Xin lỗi');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q3, 1, 'SINGLE_CHOICE', '谢谢 đọc như thế nào?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q3 AND order_index=1);
SET @qq3_1 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q3 AND order_index=1);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_1, 'Zàijiàn', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_1 AND content='Zàijiàn');
SET @op3_1_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_1 AND content='Zàijiàn');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_1, 'Xièxie', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_1 AND content='Xièxie');
SET @op3_1_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_1 AND content='Xièxie');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_1, 'Nǐ hǎo', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_1 AND content='Nǐ hǎo');
SET @op3_1_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_1 AND content='Nǐ hǎo');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_1, 'Duìbuqǐ', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_1 AND content='Duìbuqǐ');
SET @op3_1_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_1 AND content='Duìbuqǐ');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q3, 2, 'SINGLE_CHOICE', 'Tạm biệt trong tiếng Trung là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q3 AND order_index=2);
SET @qq3_2 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q3 AND order_index=2);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_2, '你好', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_2 AND content='你好');
SET @op3_2_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_2 AND content='你好');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_2, '谢谢', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_2 AND content='谢谢');
SET @op3_2_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_2 AND content='谢谢');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_2, '再见', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_2 AND content='再见');
SET @op3_2_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_2 AND content='再见');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_2, '我', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_2 AND content='我');
SET @op3_2_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_2 AND content='我');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q3, 3, 'SINGLE_CHOICE', '我 có nghĩa là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q3 AND order_index=3);
SET @qq3_3 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q3 AND order_index=3);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_3, 'Bạn', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_3 AND content='Bạn');
SET @op3_3_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_3 AND content='Bạn');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_3, 'Anh ấy', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_3 AND content='Anh ấy');
SET @op3_3_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_3 AND content='Anh ấy');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_3, 'Cô ấy', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_3 AND content='Cô ấy');
SET @op3_3_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_3 AND content='Cô ấy');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_3, 'Tôi', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_3 AND content='Tôi');
SET @op3_3_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_3 AND content='Tôi');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q3, 4, 'SINGLE_CHOICE', 'Số một trong tiếng Trung là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q3 AND order_index=4);
SET @qq3_4 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q3 AND order_index=4);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_4, '一', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_4 AND content='一');
SET @op3_4_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_4 AND content='一');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_4, '二', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_4 AND content='二');
SET @op3_4_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_4 AND content='二');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_4, '三', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_4 AND content='三');
SET @op3_4_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_4 AND content='三');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq3_4, '十', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq3_4 AND content='十');
SET @op3_4_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq3_4 AND content='十');
INSERT INTO quizes (title, quiz_type, pass_score, time_limit, max_attempts, max_pauses)
SELECT 'Kiểm tra cuối khóa HSK 1', 'EXAM', 60, 15, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM quizes WHERE title='Kiểm tra cuối khóa HSK 1');
SET @q4 = (SELECT MIN(quizes_id) FROM quizes WHERE title='Kiểm tra cuối khóa HSK 1');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q4, 0, 'SINGLE_CHOICE', '你好 có nghĩa là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q4 AND order_index=0);
SET @qq4_0 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q4 AND order_index=0);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_0, 'Xin chào', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_0 AND content='Xin chào');
SET @op4_0_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_0 AND content='Xin chào');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_0, 'Cảm ơn', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_0 AND content='Cảm ơn');
SET @op4_0_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_0 AND content='Cảm ơn');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_0, 'Tạm biệt', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_0 AND content='Tạm biệt');
SET @op4_0_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_0 AND content='Tạm biệt');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_0, 'Xin lỗi', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_0 AND content='Xin lỗi');
SET @op4_0_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_0 AND content='Xin lỗi');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q4, 1, 'SINGLE_CHOICE', '谢谢 đọc như thế nào?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q4 AND order_index=1);
SET @qq4_1 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q4 AND order_index=1);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_1, 'Zàijiàn', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_1 AND content='Zàijiàn');
SET @op4_1_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_1 AND content='Zàijiàn');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_1, 'Xièxie', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_1 AND content='Xièxie');
SET @op4_1_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_1 AND content='Xièxie');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_1, 'Nǐ hǎo', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_1 AND content='Nǐ hǎo');
SET @op4_1_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_1 AND content='Nǐ hǎo');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_1, 'Duìbuqǐ', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_1 AND content='Duìbuqǐ');
SET @op4_1_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_1 AND content='Duìbuqǐ');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q4, 2, 'SINGLE_CHOICE', 'Tạm biệt trong tiếng Trung là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q4 AND order_index=2);
SET @qq4_2 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q4 AND order_index=2);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_2, '你好', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_2 AND content='你好');
SET @op4_2_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_2 AND content='你好');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_2, '谢谢', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_2 AND content='谢谢');
SET @op4_2_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_2 AND content='谢谢');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_2, '再见', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_2 AND content='再见');
SET @op4_2_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_2 AND content='再见');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_2, '我', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_2 AND content='我');
SET @op4_2_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_2 AND content='我');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q4, 3, 'SINGLE_CHOICE', '我 có nghĩa là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q4 AND order_index=3);
SET @qq4_3 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q4 AND order_index=3);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_3, 'Bạn', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_3 AND content='Bạn');
SET @op4_3_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_3 AND content='Bạn');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_3, 'Anh ấy', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_3 AND content='Anh ấy');
SET @op4_3_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_3 AND content='Anh ấy');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_3, 'Cô ấy', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_3 AND content='Cô ấy');
SET @op4_3_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_3 AND content='Cô ấy');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_3, 'Tôi', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_3 AND content='Tôi');
SET @op4_3_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_3 AND content='Tôi');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q4, 4, 'SINGLE_CHOICE', 'Số một trong tiếng Trung là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q4 AND order_index=4);
SET @qq4_4 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q4 AND order_index=4);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_4, '一', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_4 AND content='一');
SET @op4_4_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_4 AND content='一');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_4, '二', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_4 AND content='二');
SET @op4_4_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_4 AND content='二');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_4, '三', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_4 AND content='三');
SET @op4_4_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_4 AND content='三');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq4_4, '十', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq4_4 AND content='十');
SET @op4_4_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq4_4 AND content='十');
INSERT INTO course_contents (chapter_id, quiz_id, content_type, order_index, is_preview)
SELECT @ch1_2, @q4, 'QUIZ', 3, FALSE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch1_2 AND quiz_id=@q4);
INSERT INTO quizes (title, quiz_type, pass_score, time_limit, max_attempts, max_pauses)
SELECT 'Kiểm tra giao tiếp', 'EXAM', 60, 15, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM quizes WHERE title='Kiểm tra giao tiếp');
SET @q5 = (SELECT MIN(quizes_id) FROM quizes WHERE title='Kiểm tra giao tiếp');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q5, 0, 'SINGLE_CHOICE', '你好 có nghĩa là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q5 AND order_index=0);
SET @qq5_0 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q5 AND order_index=0);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_0, 'Xin chào', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_0 AND content='Xin chào');
SET @op5_0_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_0 AND content='Xin chào');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_0, 'Cảm ơn', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_0 AND content='Cảm ơn');
SET @op5_0_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_0 AND content='Cảm ơn');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_0, 'Tạm biệt', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_0 AND content='Tạm biệt');
SET @op5_0_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_0 AND content='Tạm biệt');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_0, 'Xin lỗi', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_0 AND content='Xin lỗi');
SET @op5_0_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_0 AND content='Xin lỗi');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q5, 1, 'SINGLE_CHOICE', '谢谢 đọc như thế nào?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q5 AND order_index=1);
SET @qq5_1 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q5 AND order_index=1);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_1, 'Zàijiàn', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_1 AND content='Zàijiàn');
SET @op5_1_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_1 AND content='Zàijiàn');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_1, 'Xièxie', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_1 AND content='Xièxie');
SET @op5_1_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_1 AND content='Xièxie');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_1, 'Nǐ hǎo', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_1 AND content='Nǐ hǎo');
SET @op5_1_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_1 AND content='Nǐ hǎo');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_1, 'Duìbuqǐ', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_1 AND content='Duìbuqǐ');
SET @op5_1_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_1 AND content='Duìbuqǐ');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q5, 2, 'SINGLE_CHOICE', 'Tạm biệt trong tiếng Trung là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q5 AND order_index=2);
SET @qq5_2 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q5 AND order_index=2);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_2, '你好', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_2 AND content='你好');
SET @op5_2_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_2 AND content='你好');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_2, '谢谢', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_2 AND content='谢谢');
SET @op5_2_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_2 AND content='谢谢');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_2, '再见', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_2 AND content='再见');
SET @op5_2_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_2 AND content='再见');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_2, '我', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_2 AND content='我');
SET @op5_2_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_2 AND content='我');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q5, 3, 'SINGLE_CHOICE', '我 có nghĩa là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q5 AND order_index=3);
SET @qq5_3 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q5 AND order_index=3);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_3, 'Bạn', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_3 AND content='Bạn');
SET @op5_3_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_3 AND content='Bạn');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_3, 'Anh ấy', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_3 AND content='Anh ấy');
SET @op5_3_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_3 AND content='Anh ấy');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_3, 'Cô ấy', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_3 AND content='Cô ấy');
SET @op5_3_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_3 AND content='Cô ấy');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_3, 'Tôi', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_3 AND content='Tôi');
SET @op5_3_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_3 AND content='Tôi');
INSERT INTO quiz_questions (quiz_id, order_index, question_type, content)
SELECT @q5, 4, 'SINGLE_CHOICE', 'Số một trong tiếng Trung là gì?'
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE quiz_id=@q5 AND order_index=4);
SET @qq5_4 = (SELECT MIN(quiz_question_id) FROM quiz_questions WHERE quiz_id=@q5 AND order_index=4);
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_4, '一', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_4 AND content='一');
SET @op5_4_0 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_4 AND content='一');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_4, '二', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_4 AND content='二');
SET @op5_4_1 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_4 AND content='二');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_4, '三', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_4 AND content='三');
SET @op5_4_2 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_4 AND content='三');
INSERT INTO quiz_options (quiz_question_id, content, is_correct)
SELECT @qq5_4, '十', FALSE
WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_question_id=@qq5_4 AND content='十');
SET @op5_4_3 = (SELECT MIN(quiz_option_id) FROM quiz_options WHERE quiz_question_id=@qq5_4 AND content='十');
INSERT INTO course_contents (chapter_id, quiz_id, content_type, order_index, is_preview)
SELECT @ch3_2, @q5, 'QUIZ', 3, FALSE
WHERE NOT EXISTS (SELECT 1 FROM course_contents WHERE chapter_id=@ch3_2 AND quiz_id=@q5);

-- VOUCHER VÀ NỘI DUNG HIỂN THỊ
INSERT INTO voucher (code, type, discount, min_order, max_discount, max_usage, used_count, active, expired_at)
SELECT 'JLER10', 'PERCENT', 10, 100000, 50000, 1000, 0, TRUE, '2099-12-31'
WHERE NOT EXISTS (SELECT 1 FROM voucher WHERE code='JLER10');
INSERT INTO voucher (code, type, discount, min_order, max_discount, max_usage, used_count, active, expired_at)
SELECT 'STUDY20', 'PERCENT', 20, 300000, 100000, 1000, 0, TRUE, '2099-12-31'
WHERE NOT EXISTS (SELECT 1 FROM voucher WHERE code='STUDY20');
INSERT INTO voucher (code, type, discount, min_order, max_discount, max_usage, used_count, active, expired_at)
SELECT 'FIX30', 'FIXED', 30000, 200000, NULL, 1000, 0, TRUE, '2099-12-31'
WHERE NOT EXISTS (SELECT 1 FROM voucher WHERE code='FIX30');
SET @v10 = (SELECT MIN(id) FROM voucher WHERE code='JLER10');
INSERT INTO platform_post (type, title, content, image_url, active, created_at)
SELECT 'BANNER', 'Chào mừng đến với JLer', 'Bắt đầu hành trình tiếng Trung cùng sách, bài học và bài luyện tập của JLer.', '/uploads/jler-media/study-desk.png', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM platform_post WHERE title='Chào mừng đến với JLer');
INSERT INTO platform_post (type, title, content, image_url, active, created_at)
SELECT 'BANNER', 'Mỗi ngày một bước tiến', 'Dành một khoảng thời gian nhỏ để ôn từ và luyện giao tiếp mỗi ngày.', '/uploads/jler-media/hsk-books.png', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM platform_post WHERE title='Mỗi ngày một bước tiến');
INSERT INTO platform_post (type, title, content, image_url, active, created_at)
SELECT 'NOTICE', 'Ưu đãi học tập', 'Áp mã JLER10 cho đơn từ 100.000đ, giảm 10%, tối đa 50.000đ.', '/uploads/jler-media/conversation.png', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM platform_post WHERE title='Ưu đãi học tập');
INSERT INTO platform_post (type, title, content, image_url, active, created_at)
SELECT 'NOTICE', 'Thanh toán và nhận sách', 'Sách hỗ trợ COD; khóa học có thể thanh toán bằng SePay khi đơn vị vận hành đã bật cấu hình.', NULL, TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM platform_post WHERE title='Thanh toán và nhận sách');
INSERT INTO platform_post (type, title, content, image_url, active, created_at)
SELECT 'BLOG', 'Bắt đầu học tiếng Trung từ đâu?', 'Bắt đầu với Pinyin và thanh điệu. Mỗi ngày nghe và đọc to vài câu ngắn. Sau đó chọn một giáo trình phù hợp để đi theo lộ trình.', NULL, TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM platform_post WHERE title='Bắt đầu học tiếng Trung từ đâu?');
INSERT INTO platform_post (type, title, content, image_url, active, created_at)
SELECT 'BLOG', 'Cách ghi nhớ từ vựng', 'Học từ theo ngữ cảnh, sử dụng flashcard và ôn lại sau một khoảng thời gian. Tự đặt câu với những từ đã học.', NULL, TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM platform_post WHERE title='Cách ghi nhớ từ vựng');
INSERT INTO platform_post (type, title, content, image_url, active, created_at)
SELECT 'BLOG', 'Chuẩn bị cho kỳ thi HSK', 'Ôn tập từ vựng, ngữ pháp và luyện đề có giới hạn thời gian. Xem lại câu sai để hiểu nguyên nhân.', NULL, TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM platform_post WHERE title='Chuẩn bị cho kỳ thi HSK');

-- ĐƠN HÀNG MẪU – KHÔNG TẠO GIAO DỊCH SEPAY GIẢ
INSERT INTO orders (order_code, user_id, status, payment_status, shipping_status, payment_method, has_physical_item, subtotal, discount, shipping_fee, total_amount, voucher_id, transaction_id, payment_at, created_at, note)
SELECT 'JLDEMO000000000001', @u1, 'COMPLETED', 'paid', 'delivered', 'COD', TRUE, 240000, 0, 30000, 270000, NULL, 'DEMO-1', '2026-11-10 19:00:00', '2026-11-10 18:00:00', '[JLer Demo] Đơn minh họa, không phải giao dịch ngân hàng thật.'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_code='JLDEMO000000000001');
SET @o1 = (SELECT MIN(order_id) FROM orders WHERE order_code='JLDEMO000000000001');
INSERT INTO order_items (order_id, item_id, item_type, quantity, price_at_purchase, item_title)
SELECT @o1, @b1, 'Book', 2, 120000, 'Giáo trình HSK 1'
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=@o1 AND item_type='Book' AND item_id=@b1);
INSERT INTO shipping_orders (order_id, full_name, phone, address)
SELECT @o1, 'Người nhận 1', '0900000001', 'Địa chỉ giao hàng minh họa, Hà Nội'
WHERE NOT EXISTS (SELECT 1 FROM shipping_orders WHERE order_id=@o1);
SET @demo_order2_existed = (SELECT COUNT(*) FROM orders WHERE order_code='JLDEMO000000000002');
INSERT INTO orders (order_code, user_id, status, payment_status, shipping_status, payment_method, has_physical_item, subtotal, discount, shipping_fee, total_amount, voucher_id, transaction_id, payment_at, created_at, note)
SELECT 'JLDEMO000000000002', @u1, 'COMPLETED', 'paid', 'none', 'BANK_TRANSFER', FALSE, 199000, 19900, 0, 179100, @v10, 'DEMO-2', '2026-11-10 19:00:00', '2026-11-10 18:00:00', '[JLer Demo] Đơn minh họa, không phải giao dịch ngân hàng thật.'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_code='JLDEMO000000000002');
SET @o2 = (SELECT MIN(order_id) FROM orders WHERE order_code='JLDEMO000000000002');
INSERT INTO order_items (order_id, item_id, item_type, quantity, price_at_purchase, item_title)
SELECT @o2, @c1, 'Course', 1, 199000, 'HSK 1 – Tiếng Trung từ đầu'
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=@o2 AND item_type='Course' AND item_id=@c1);
INSERT INTO orders (order_code, user_id, status, payment_status, shipping_status, payment_method, has_physical_item, subtotal, discount, shipping_fee, total_amount, voucher_id, transaction_id, payment_at, created_at, note)
SELECT 'JLDEMO000000000003', @u2, 'PENDING', 'pending', 'preparing', 'COD', TRUE, 140000, 0, 30000, 170000, NULL, NULL, NULL, '2026-11-10 18:00:00', '[JLer Demo] Đơn minh họa, không phải giao dịch ngân hàng thật.'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_code='JLDEMO000000000003');
SET @o3 = (SELECT MIN(order_id) FROM orders WHERE order_code='JLDEMO000000000003');
INSERT INTO order_items (order_id, item_id, item_type, quantity, price_at_purchase, item_title)
SELECT @o3, @b2, 'Book', 1, 140000, 'Bài tập HSK 1'
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=@o3 AND item_type='Book' AND item_id=@b2);
INSERT INTO shipping_orders (order_id, full_name, phone, address)
SELECT @o3, 'Người nhận 2', '0900000002', 'Địa chỉ giao hàng minh họa, Hà Nội'
WHERE NOT EXISTS (SELECT 1 FROM shipping_orders WHERE order_id=@o3);
INSERT INTO orders (order_code, user_id, status, payment_status, shipping_status, payment_method, has_physical_item, subtotal, discount, shipping_fee, total_amount, voucher_id, transaction_id, payment_at, created_at, note)
SELECT 'JLDEMO000000000004', @u1, 'PREPARING', 'pending', 'preparing', 'COD', TRUE, 160000, 0, 30000, 190000, NULL, NULL, NULL, '2026-11-10 18:00:00', '[JLer Demo] Đơn minh họa, không phải giao dịch ngân hàng thật.'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_code='JLDEMO000000000004');
SET @o4 = (SELECT MIN(order_id) FROM orders WHERE order_code='JLDEMO000000000004');
INSERT INTO order_items (order_id, item_id, item_type, quantity, price_at_purchase, item_title)
SELECT @o4, @b3, 'Book', 1, 160000, 'Giáo trình HSK 2'
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=@o4 AND item_type='Book' AND item_id=@b3);
INSERT INTO shipping_orders (order_id, full_name, phone, address)
SELECT @o4, 'Người nhận 1', '0900000001', 'Địa chỉ giao hàng minh họa, Hà Nội'
WHERE NOT EXISTS (SELECT 1 FROM shipping_orders WHERE order_id=@o4);
INSERT INTO orders (order_code, user_id, status, payment_status, shipping_status, payment_method, has_physical_item, subtotal, discount, shipping_fee, total_amount, voucher_id, transaction_id, payment_at, created_at, note)
SELECT 'JLDEMO000000000005', @u2, 'SHIPPING', 'paid', 'shipping', 'COD', TRUE, 180000, 0, 30000, 210000, NULL, 'DEMO-5', '2026-11-10 19:00:00', '2026-11-10 18:00:00', '[JLer Demo] Đơn minh họa, không phải giao dịch ngân hàng thật.'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_code='JLDEMO000000000005');
SET @o5 = (SELECT MIN(order_id) FROM orders WHERE order_code='JLDEMO000000000005');
INSERT INTO order_items (order_id, item_id, item_type, quantity, price_at_purchase, item_title)
SELECT @o5, @b4, 'Book', 1, 180000, 'Từ vựng HSK 2'
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=@o5 AND item_type='Book' AND item_id=@b4);
INSERT INTO shipping_orders (order_id, full_name, phone, address)
SELECT @o5, 'Người nhận 2', '0900000002', 'Địa chỉ giao hàng minh họa, Hà Nội'
WHERE NOT EXISTS (SELECT 1 FROM shipping_orders WHERE order_id=@o5);
INSERT INTO orders (order_code, user_id, status, payment_status, shipping_status, payment_method, has_physical_item, subtotal, discount, shipping_fee, total_amount, voucher_id, transaction_id, payment_at, created_at, note)
SELECT 'JLDEMO000000000006', @u2, 'CANCELED', 'pending', 'cancelled', 'COD', TRUE, 200000, 0, 30000, 230000, NULL, NULL, NULL, '2026-11-10 18:00:00', '[JLer Demo] Đơn minh họa, không phải giao dịch ngân hàng thật.'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_code='JLDEMO000000000006');
SET @o6 = (SELECT MIN(order_id) FROM orders WHERE order_code='JLDEMO000000000006');
INSERT INTO order_items (order_id, item_id, item_type, quantity, price_at_purchase, item_title)
SELECT @o6, @b5, 'Book', 1, 200000, 'Bộ đề HSK 3'
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=@o6 AND item_type='Book' AND item_id=@b5);
INSERT INTO shipping_orders (order_id, full_name, phone, address)
SELECT @o6, 'Người nhận 2', '0900000002', 'Địa chỉ giao hàng minh họa, Hà Nội'
WHERE NOT EXISTS (SELECT 1 FROM shipping_orders WHERE order_id=@o6);
INSERT INTO orders (order_code, user_id, status, payment_status, shipping_status, payment_method, has_physical_item, subtotal, discount, shipping_fee, total_amount, voucher_id, transaction_id, payment_at, created_at, note)
SELECT 'JLDEMO000000000007', @u2, 'PENDING', 'pending', 'none', 'BANK_TRANSFER', FALSE, 398000, 0, 0, 398000, NULL, NULL, NULL, '2026-11-10 18:00:00', '[JLer Demo] Đơn minh họa, không phải giao dịch ngân hàng thật.'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_code='JLDEMO000000000007');
SET @o7 = (SELECT MIN(order_id) FROM orders WHERE order_code='JLDEMO000000000007');
INSERT INTO order_items (order_id, item_id, item_type, quantity, price_at_purchase, item_title)
SELECT @o7, @c2, 'Course', 1, 398000, 'HSK 2 – Vững nền tảng'
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=@o7 AND item_type='Course' AND item_id=@c2);
INSERT INTO orders (order_code, user_id, status, payment_status, shipping_status, payment_method, has_physical_item, subtotal, discount, shipping_fee, total_amount, voucher_id, transaction_id, payment_at, created_at, note)
SELECT 'JLDEMO000000000008', @u2, 'COMPLETED', 'paid', 'none', 'BANK_TRANSFER', FALSE, 597000, 0, 0, 597000, NULL, 'DEMO-8', '2026-11-10 19:00:00', '2026-11-10 18:00:00', '[JLer Demo] Đơn minh họa, không phải giao dịch ngân hàng thật.'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_code='JLDEMO000000000008');
SET @o8 = (SELECT MIN(order_id) FROM orders WHERE order_code='JLDEMO000000000008');
INSERT INTO order_items (order_id, item_id, item_type, quantity, price_at_purchase, item_title)
SELECT @o8, @c3, 'Course', 1, 597000, 'Giao tiếp tiếng Trung'
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=@o8 AND item_type='Course' AND item_id=@c3);
UPDATE voucher SET used_count=COALESCE(used_count,0)+1 WHERE id=@v10 AND @demo_order2_existed=0;
INSERT INTO user_courses (user_id, course_id, status, enrolled_at)
SELECT @u1, @c1, 'ACTIVE', '2026-11-10 19:00:00'
WHERE NOT EXISTS (SELECT 1 FROM user_courses WHERE user_id=@u1 AND course_id=@c1);
INSERT INTO user_courses (user_id, course_id, status, enrolled_at)
SELECT @u2, @c3, 'ACTIVE', '2026-11-10 19:00:00'
WHERE NOT EXISTS (SELECT 1 FROM user_courses WHERE user_id=@u2 AND course_id=@c3);

-- GIỎ HÀNG / YÊU THÍCH / TIẾN ĐỘ / TỪ VỰNG
INSERT INTO carts (user_id, created_at, update_at)
SELECT @u1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM carts WHERE user_id=@u1);
SET @cart1 = (SELECT MIN(cart_id) FROM carts WHERE user_id=@u1);
INSERT INTO cart_items (cart_id, item_id, item_type, quantity, price_at_purchase, created_at)
SELECT @cart1, @b7, 'Book', 2, 240000, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM cart_items WHERE cart_id=@cart1 AND item_id=@b7 AND item_type='Book');
INSERT INTO cart_items (cart_id, item_id, item_type, quantity, price_at_purchase, created_at)
SELECT @cart1, @c2, 'Course', 1, 398000, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM cart_items WHERE cart_id=@cart1 AND item_id=@c2 AND item_type='Course');
INSERT INTO favorite (user_id, item_id, item_type)
SELECT @u1, @b2, 'Book'
WHERE NOT EXISTS (SELECT 1 FROM favorite WHERE user_id=@u1 AND item_id=@b2 AND item_type='Book');
INSERT INTO favorite (user_id, item_id, item_type)
SELECT @u1, @c2, 'Course'
WHERE NOT EXISTS (SELECT 1 FROM favorite WHERE user_id=@u1 AND item_id=@c2 AND item_type='Course');
INSERT INTO saved_word (user_id, front, back, mastered)
SELECT @u1, '你好 · nǐ hǎo', 'Xin chào', TRUE
WHERE NOT EXISTS (SELECT 1 FROM saved_word WHERE user_id=@u1 AND front='你好 · nǐ hǎo');
INSERT INTO saved_word (user_id, front, back, mastered)
SELECT @u1, '学习 · xuéxí', 'Học tập', FALSE
WHERE NOT EXISTS (SELECT 1 FROM saved_word WHERE user_id=@u1 AND front='学习 · xuéxí');
INSERT INTO saved_word (user_id, front, back, mastered)
SELECT @u1, '朋友 · péngyou', 'Bạn bè', FALSE
WHERE NOT EXISTS (SELECT 1 FROM saved_word WHERE user_id=@u1 AND front='朋友 · péngyou');
INSERT INTO carts (user_id, created_at, update_at)
SELECT @u2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM carts WHERE user_id=@u2);
SET @cart2 = (SELECT MIN(cart_id) FROM carts WHERE user_id=@u2);
INSERT INTO cart_items (cart_id, item_id, item_type, quantity, price_at_purchase, created_at)
SELECT @cart2, @b8, 'Book', 1, 260000, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM cart_items WHERE cart_id=@cart2 AND item_id=@b8 AND item_type='Book');
INSERT INTO cart_items (cart_id, item_id, item_type, quantity, price_at_purchase, created_at)
SELECT @cart2, @c4, 'Course', 1, 796000, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM cart_items WHERE cart_id=@cart2 AND item_id=@c4 AND item_type='Course');
INSERT INTO favorite (user_id, item_id, item_type)
SELECT @u2, @b1, 'Book'
WHERE NOT EXISTS (SELECT 1 FROM favorite WHERE user_id=@u2 AND item_id=@b1 AND item_type='Book');
INSERT INTO favorite (user_id, item_id, item_type)
SELECT @u2, @c1, 'Course'
WHERE NOT EXISTS (SELECT 1 FROM favorite WHERE user_id=@u2 AND item_id=@c1 AND item_type='Course');
INSERT INTO saved_word (user_id, front, back, mastered)
SELECT @u2, '你好 · nǐ hǎo', 'Xin chào', TRUE
WHERE NOT EXISTS (SELECT 1 FROM saved_word WHERE user_id=@u2 AND front='你好 · nǐ hǎo');
INSERT INTO saved_word (user_id, front, back, mastered)
SELECT @u2, '学习 · xuéxí', 'Học tập', FALSE
WHERE NOT EXISTS (SELECT 1 FROM saved_word WHERE user_id=@u2 AND front='学习 · xuéxí');
INSERT INTO saved_word (user_id, front, back, mastered)
SELECT @u2, '朋友 · péngyou', 'Bạn bè', FALSE
WHERE NOT EXISTS (SELECT 1 FROM saved_word WHERE user_id=@u2 AND front='朋友 · péngyou');
INSERT INTO user_lesson_progress (user_id, lesson_id, is_completed, last_position, last_accessed_at, completed_at)
SELECT @u1, @l1, TRUE, 600, '2026-11-10 20:00:00', '2026-11-10 20:00:00'
WHERE NOT EXISTS (SELECT 1 FROM user_lesson_progress WHERE user_id=@u1 AND lesson_id=@l1);
INSERT INTO user_lesson_progress (user_id, lesson_id, is_completed, last_position, last_accessed_at, completed_at)
SELECT @u1, @l2, FALSE, 180, '2026-11-10 20:00:00', NULL
WHERE NOT EXISTS (SELECT 1 FROM user_lesson_progress WHERE user_id=@u1 AND lesson_id=@l2);
INSERT INTO user_lesson_progress (user_id, lesson_id, is_completed, last_position, last_accessed_at, completed_at)
SELECT @u2, @l9, TRUE, 600, '2026-11-10 20:00:00', '2026-11-10 20:00:00'
WHERE NOT EXISTS (SELECT 1 FROM user_lesson_progress WHERE user_id=@u2 AND lesson_id=@l9);
INSERT INTO comments (user_id, lesson_id, content, created_at)
SELECT @u1, @l1, 'Bài học chào hỏi rất dễ hiểu.', '2026-11-10 20:00:00'
WHERE NOT EXISTS (SELECT 1 FROM comments WHERE user_id=@u1 AND lesson_id=@l1 AND content='Bài học chào hỏi rất dễ hiểu.');
INSERT INTO comments (user_id, lesson_id, content, created_at)
SELECT @u1, @l2, 'Em muốn luyện thêm cách hỏi giá.', '2026-11-10 20:00:00'
WHERE NOT EXISTS (SELECT 1 FROM comments WHERE user_id=@u1 AND lesson_id=@l2 AND content='Em muốn luyện thêm cách hỏi giá.');
INSERT INTO comments (user_id, lesson_id, content, created_at)
SELECT @u2, @l9, 'Đã học và ôn bằng flashcard.', '2026-11-10 20:00:00'
WHERE NOT EXISTS (SELECT 1 FROM comments WHERE user_id=@u2 AND lesson_id=@l9 AND content='Đã học và ôn bằng flashcard.');

-- MỘT KẾT QUẢ THI MẪU ĐỂ XEM LỊCH SỬ
INSERT INTO quiz_results (user_id, quizes_id, score, total_questions, correct_answers, wrong_answers, time_spent, status, submitted_at)
SELECT @u1, @q1, 80, 5, 4, 1, 120, 'PASSED', '2026-11-10 20:00:00'
WHERE NOT EXISTS (SELECT 1 FROM quiz_results WHERE user_id=@u1 AND quizes_id=@q1 AND submitted_at='2026-11-10 20:00:00');
SET @result1 = (SELECT MIN(quiz_results_id) FROM quiz_results WHERE user_id=@u1 AND quizes_id=@q1 AND submitted_at='2026-11-10 20:00:00');
INSERT INTO quiz_attempt_details (quiz_result_id, quiz_question_id, selected_option_id, is_correct, order_index)
SELECT @result1, @qq1_0, @op1_0_0, TRUE, 0
WHERE NOT EXISTS (SELECT 1 FROM quiz_attempt_details WHERE quiz_result_id=@result1 AND quiz_question_id=@qq1_0);
INSERT INTO quiz_attempt_details (quiz_result_id, quiz_question_id, selected_option_id, is_correct, order_index)
SELECT @result1, @qq1_1, @op1_1_1, TRUE, 1
WHERE NOT EXISTS (SELECT 1 FROM quiz_attempt_details WHERE quiz_result_id=@result1 AND quiz_question_id=@qq1_1);
INSERT INTO quiz_attempt_details (quiz_result_id, quiz_question_id, selected_option_id, is_correct, order_index)
SELECT @result1, @qq1_2, @op1_2_2, TRUE, 2
WHERE NOT EXISTS (SELECT 1 FROM quiz_attempt_details WHERE quiz_result_id=@result1 AND quiz_question_id=@qq1_2);
INSERT INTO quiz_attempt_details (quiz_result_id, quiz_question_id, selected_option_id, is_correct, order_index)
SELECT @result1, @qq1_3, @op1_3_3, TRUE, 3
WHERE NOT EXISTS (SELECT 1 FROM quiz_attempt_details WHERE quiz_result_id=@result1 AND quiz_question_id=@qq1_3);
INSERT INTO quiz_attempt_details (quiz_result_id, quiz_question_id, selected_option_id, is_correct, order_index)
SELECT @result1, @qq1_4, @op1_4_1, FALSE, 4
WHERE NOT EXISTS (SELECT 1 FROM quiz_attempt_details WHERE quiz_result_id=@result1 AND quiz_question_id=@qq1_4);
UPDATE book_images SET image_url='/uploads/jler-media/study-desk.png' WHERE image_url='/uploads/demo-cover-0.svg';
UPDATE courses SET thumbnail_url='/uploads/jler-media/study-desk.png' WHERE thumbnail_url='/uploads/demo-cover-0.svg';
UPDATE platform_post SET image_url='/uploads/jler-media/study-desk.png' WHERE image_url='/uploads/demo-cover-0.svg';
UPDATE book_images SET image_url='/uploads/jler-media/hsk-books.png' WHERE image_url='/uploads/demo-cover-1.svg';
UPDATE courses SET thumbnail_url='/uploads/jler-media/hsk-books.png' WHERE thumbnail_url='/uploads/demo-cover-1.svg';
UPDATE platform_post SET image_url='/uploads/jler-media/hsk-books.png' WHERE image_url='/uploads/demo-cover-1.svg';
UPDATE book_images SET image_url='/uploads/jler-media/conversation.png' WHERE image_url='/uploads/demo-cover-2.svg';
UPDATE courses SET thumbnail_url='/uploads/jler-media/conversation.png' WHERE thumbnail_url='/uploads/demo-cover-2.svg';
UPDATE platform_post SET image_url='/uploads/jler-media/conversation.png' WHERE image_url='/uploads/demo-cover-2.svg';
UPDATE lessons SET video_url='/uploads/jler-media/chao-hoi.mp4',duration=42 WHERE title IN ('Chào hỏi và giới thiệu','[Mẫu] Chào hỏi và giới thiệu') AND (video_url IS NULL OR TRIM(video_url)='');
UPDATE lessons SET video_url='/uploads/jler-media/so-dem.mp4',duration=72 WHERE title IN ('Đếm số và hỏi giá','[Mẫu] Đếm số và hỏi giá') AND (video_url IS NULL OR TRIM(video_url)='');
COMMIT;
UPDATE users
SET password = '$2a$10$nKwWm/6jqtbmq.DuKDDvbOPd690J31bQST9OU2FbNm9VEAToSP5Eu',
    status = TRUE,
    token_version = COALESCE(token_version, 0) + 1
WHERE username = 'admin';