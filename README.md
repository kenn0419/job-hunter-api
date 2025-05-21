# Job Hunter

**Job Hunter** là một ứng dụng Java được xây dựng với Maven, nhằm mục đích hỗ trợ người dùng tìm kiếm việc làm một cách hiệu quả. Dự án này hiện đang trong giai đoạn phát triển ban đầu.
## 📁 Cấu trúc dự án
job-hunter/  
├── .mvn/   📂 # Thư mục cấu hình Maven Wrapper  
├── src/   📂 # Mã nguồn chính  
│ ├── main/ 📂  
│ │ └── java/   📂 # Mã nguồn Java  
│ └── test/   📂 # Mã nguồn kiểm thử  
├── .gitignore 📄 # Tệp cấu hình Git  
├── mvnw 📄 # Maven Wrapper cho Unix  
├── mvnw.cmd 📄 # Maven Wrapper cho Windows  
└── pom.xml 📄 # Tệp cấu hình Maven  


## 🚀 Tính năng chính

- Đăng ký / đăng nhập người dùng (ứng viên, nhà tuyển dụng)
- Quản lý hồ sơ ứng viên
- Tạo và quản lý bài đăng tuyển dụng
- Ứng tuyển công việc và theo dõi trạng thái
- Tìm kiếm công việc
- Tính năng xác thực bằng JWT

## 🛠️ Công nghệ sử dụng

- **Backend**: Java, Spring Framework
- **Cơ sở dữ liệu**: MySQL
- **Quản lý phiên bản**: Git
- **Trình quản lý dự án**: Maven

## ⚙️ Cài đặt dự án

### 1. Clone repository

```bash
git clone https://github.com/kenn0419/job-hunter-api.git
```
### 2. Cài đặt
cd job-hunter-api
#### Cài đặt các phụ thuộc và chạy ứng dụng
mvn install
mvn spring-boot:run

