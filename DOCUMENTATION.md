# DressCode Backend API Documentation

This document describes the API endpoints for the DressCode backend application. All API endpoints are prefixed with `/api`.

**Base URL**: `http://localhost:8080/api` (or your deployed backend URL)

---

## 1. Authentication (认证)

### 1.1 User Registration (用户注册)

- **Endpoint**: `/auth/register`
- **Method**: `POST`
- **Authentication**: None required
- **Request Body (JSON)**:
  ```json
  {
    "username": "unique_username",
    "password": "strong_password",
    "nickname": "Your Nickname",
    "gender": "male" | "female" | "unknown",
    "avatar": "http://example.com/avatar.jpg" (optional)
  }
  ```
- **Response (JSON)**:
  - **Success (201 Created)**:
    ```json
    {
      "code": 0,
      "message": "用户注册成功"
    }
    ```

### 1.2 User Login (用户登录)

- **Endpoint**: `/auth/login`
- **Method**: `POST`
- **Authentication**: None required
- **Request Body (JSON)**:
  ```json
  {
    "username": "your_username",
    "password": "your_password"
  }
  ```
- **Response (JSON)**:
  - **Success (200 OK)**:
    ```json
    {
      "code": 0,
      "message": "登录成功",
      "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "user": {
          "id": 1,
          "username": "your_username",
          "nickname": "Your Nickname",
          // ...
        }
      }
    }
    ```

### 1.3 Get Current User Info (获取当前用户信息)

- **Endpoint**: `/auth/me`
- **Method**: `GET`
- **Authentication**: **Required** (JWT in `Authorization: Bearer <token>`)
- **Response (JSON)**:
  - **Success (200 OK)**:
    ```json
    {
      "code": 0,
      "message": "获取用户信息成功",
      "data": {
        "id": 1,
        "username": "your_username",
        // ...
      }
    }
    ```

---

## 2. File Upload (文件上传)

### 2.1 Upload File to Kodo (上传文件到七牛云Kodo)

- **Endpoint**: `/upload`
- **Method**: `POST`
- **Authentication**: **Required**
- **Request Body (multipart/form-data)**:
  - `file`: The file to upload (type `file`).
- **Response (JSON)**:
  - **Success (200 OK)**:
    ```json
    {
      "code": 0,
      "message": "文件上传成功",
      "data": {
        "url": "https://your_qiniu_domain/uploads/path/to/your/file.jpg"
      }
    }
    ```

---

## 3. Outfit Posts (穿搭文章)

### 3.1 Create Post (创建文章)

- **Endpoint**: `/posts`
- **Method**: `POST`
- **Authentication**: **Required**
- **Request Body (JSON)**:
  ```json
  {
    "title": "我的第一套春日穿搭",
    "content": "这是一套适合春天的休闲穿搭...",
    "style": "休闲",
    "season": "春季",
    "scene": "日常",
    "images": ["https://qiniu.com/img1.jpg"],
    "tags": ["春日", "休闲风"]
  }
  ```
- **Response (JSON)**:
  - **Success (201 Created)**: `{"code": 0, "message": "文章创建成功"}`

### 3.2 List Posts (获取文章列表)

- **Endpoint**: `/posts`
- **Method**: `GET`
- **Authentication**: None required
- **Query Parameters**:
  - `page`, `page_size`, `style`, `season`, `scene`, `gender`, `tag`, `q`
- **Response (JSON)**:
  - **Success (200 OK)**: A paginated list of posts.

### 3.3 Get Single Post (获取单篇文章)

- **Endpoint**: `/posts/:id`
- **Method**: `GET`
- **Authentication**: None required
- **Response (JSON)**:
  - **Success (200 OK)**: Detailed information for a single post.

### 3.4 Update Post (更新文章)

- **Endpoint**: `/posts/:id`
- **Method**: `PUT`
- **Authentication**: **Required** (and must be the author)
- **Request Body (JSON)**: (Partial update supported)
  ```json
  {
    "title": "更新后的标题",
    "tags": ["新标签"]
  }
  ```
- **Response (JSON)**:
  - **Success (200 OK)**: `{"code": 0, "message": "文章更新成功"}`

### 3.5 Delete Post (删除文章)

- **Endpoint**: `/posts/:id`
- **Method**: `DELETE`
- **Authentication**: **Required** (and must be the author)
- **Response (JSON)**:
  - **Success (200 OK)**: `{"code": 0, "message": "文章删除成功"}`

### 3.6 Favorite Post (收藏文章)

- **Endpoint**: `/posts/:id/favorite`
- **Method**: `POST`
- **Authentication**: **Required**
- **Response (JSON)**:
  - **Success (200 OK)**: `{"code": 0, "message": "收藏成功"}`

### 3.7 Remove Favorite Post (取消收藏文章)

- **Endpoint**: `/posts/:id/favorite`
- **Method**: `DELETE`
- **Authentication**: **Required**
- **Response (JSON)**:
  - **Success (200 OK)**: `{"code": 0, "message": "取消收藏成功"}`

---

## 4. User Specific (用户相关)

### 4.1 List User Favorites (获取当前用户收藏列表)

- **Endpoint**: `/users/me/favorites`
- **Method**: `GET`
- **Authentication**: **Required**
- **Query Parameters**: `page`, `page_size`
- **Response (JSON)**:
  - **Success (200 OK)**: A paginated list of the user's favorited posts.

---

## 5. AI Services (AI 服务)

### 5.1 AI Image Auto-Tagging (AI图片自动打标签)

- **Endpoint**: `/ai/label-image`
- **Method**: `POST`
- **Authentication**: **Required**
- **Request Body (JSON)**:
  ```json
  {
    "image_url": "https://qiniu.com/your_uploaded_image.jpg"
  }
  ```
- **Response (JSON)**:
  - **Success (200 OK)**:
    ```json
    {
      "code": 0,
      "message": "图片标签获取成功",
      "data": {
        "labels": { "style": "casual", "season": "spring/summer", ... },
        "raw_text": "图中人物穿着..."
      }
    }
    ```

### 5.2 Start Virtual Try-On Task (启动智能换装任务)

- **Endpoint**: `/ai/virtual-try-on/start`
- **Method**: `POST`
- **Authentication**: **Required**
- **Request Body (JSON)**:
  ```json
  {
    "person_image_url": "https://qiniu.com/your_person_image.jpg",
    "clothing_image_url": "https://qiniu.com/your_clothing_image.jpg"
  }
  ```
- **Response (JSON)**:
  - **Success (200 OK)**:
    ```json
    {
      "code": 0,
      "message": "智能换装任务已启动",
      "data": {
        "internal_task_id": 1
      }
    }
    ```

### 5.3 Get Virtual Try-On Task Status (查询智能换装任务状态)

- **Endpoint**: `/ai/virtual-try-on/status/:internal_task_id`
- **Method**: `GET`
- **Authentication**: **Required**
- **URL Parameters**:
  - `internal_task_id` (int, required): The ID returned from the `/start` endpoint.
- **Response (JSON)**:
  - **Success (200 OK)**:
    - If pending:
      ```json
      {
        "code": 0,
        "message": "获取任务状态成功",
        "data": {
          "id": 1,
          "task_id": "modelscope-task-id...",
          "status": "PENDING",
          // ...
        }
      }
      ```
    - If succeeded:
       ```json
      {
        "code": 0,
        "message": "获取任务状态成功",
        "data": {
          "id": 1,
          "task_id": "modelscope-task-id...",
          "status": "SUCCEED",
          "result_url": "https://generated_image_url.jpg",
          // ...
        }
      }
      ```