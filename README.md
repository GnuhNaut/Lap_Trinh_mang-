## lẬP TRÌNH MẠNG 

## Bài tập lớn 1

# Simple Chat Application

```
SV thực hiện: 
   Trương Tuấn Hùng 
   Đặng Văn Huấn 
   Vũ Trọng Đức 
```

## Mục lục


- 1 Giới thiệu ứng dụng
- 2 Định nghĩa giao thức cho từng chức năng
- 3 Thiết kế ứng dụng
   - 3.1 Công nghệ sử dụng
   - 3.2 Kiến trúc ứng dụng
        - 3.2.1 Class Client 
        - 3.2.2 Class Server 
- 4 Đánh giá kết quả hiện thực
   - 4.1 Kết quả đạt được
   - 4.2 Những điều chưa đạt được

## 1 Giới thiệu ứng dụng

Ứng dụng là phần mềm cho phép hai hay nhiều người dùng có thể giao tiếp với nhau với các
tính năng chính: chat broadcast, chat riêng, truyền file nhóm, truyền file riêng

## 2 Định nghĩa giao thức cho từng chức năng

- SAu khi user thiết lập kết nối thì user sẽ nhận được danh sách user đang online 
- Mỗi khi có user mới thì server sẽ báo cho các user khác biết 
- Mặc định chat sẽ là broadcast với định dạng gửi đi là [All]user_name: nội dung 
- Muốn chat riêng cho từng user thì cần nhập theo định dạng -->@user_name: nội dung khi đó định dạng nhận là user_sent-->@user_rev: nội dung
- User upload file lên server theo định dạng #UPLOAD ten_file khi file upload thành công thì sẽ có 1 tin nhắn đến tất cả user thông báo file đã upload  
- Lấy danh sách file được lưu trên server với #LIST 
- tải file với #DOWNLOAD ten_file 
- truyền file cho từng user với @SEND @user_rev ten_file 
## 3 Thiết kế ứng dụng

### 3.1 Công nghệ sử dụng

* TCP Socket: Một kĩ thuật dùng để hỗ trợ lập trình các ứng dụng giao tiếp qua mạng. TCP
Socket sử dụng Stream để thực hiện quá trình truyền dữ liệu của hai máy tính đã thiết lập
kết nối.
### 3.2 Kiến trúc ứng dụng
Ứng dụng chạy trên console: 
với 2 class chính là Client và Server 
#### 3.2.1 Class Client 
Giúp kết nối người dùng với server chung 
Người dùng thiết lập kết nối cần nhập chính xác của cổng server và user name để kết nối 
Sau khi kết nối người dùng có thể sử dụng các chức năng của ứng dụng
#### 3.2.2 Class Server
Kết nối với Client 
Là nơi chuyển tiếp tin nhắn giữa các client 
Là nơi lưu trữ file chung 
## 4 Đánh giá kết quả hiện thực

### 4.1 Kết quả đạt được
* Ứng dụng được xây dựng dựa trên mô hình kết hợp giữa client-server cho việc quản lí các
user 
* Ứng dụng có các tính năng đơn giản như: chat giữa hai user, một lúc đồng thời chat với
nhiều user, gửi File trong quá trình chat.

### 4.2 Những điều chưa đạt được

* Mã nguồn còn chưa tối ưu cho ứng dụng.
* Ứng dụng còn có thể thêm các tính năng như: chat nhóm, gọi video...

