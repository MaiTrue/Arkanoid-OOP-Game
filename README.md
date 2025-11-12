## 1. Phân công nhiệm vụ

### Khánh:
- Viết UML  
- Render hình ảnh: background, thanh đỡ, bóng  
- Âm thanh  
- Power up: mở rộng thanh, nhân đôi bóng, trừ 1 mạng  
- Format code, sửa lỗi  

### Mai:
- Xử lý va chạm: bóng va chạm với thanh, bóng va chạm với tường, bóng phá vỡ gạch  
- Bảng xếp hạng  

### Quyền:
- Menu: tạo menu  
- Vẽ map các level  
- Tìm kiếm các hình ảnh cho power up  

### Kiên:
- Độ phức tạp game: hiển thị điểm, số mạng của người chơi  
- Reset lại game mới  

---

## 2. Mô tả game

### 2.1. Giới thiệu chung

- **Tên dự án:** Arkanoid OOP  
- **Mô tả:** Đây là dự án xây dựng lại trò chơi Arkanoid sử dụng nền tảng JavaFX và ngôn ngữ lập trình Java.  
  Dự án không chỉ là tái hiện lại con game cổ điển mà là áp dụng các nguyên lý Lập trình Hướng Đối tượng (OOP) được học một cách triệt để, xây dựng một kiến trúc code linh hoạt, dễ bảo trì và mở rộng.  
- **Ngôn ngữ:** Java  
- **Nền tảng:** JavaFX (dùng cho toàn bộ GUI, rendering và vòng lặp game)

---

### 2.2. Lối chơi & Các Tính năng chính

#### 2.2.1. Lối chơi cơ bản
Người chơi điều khiển một thanh đỡ (Paddle) ở cuối màn hình, di chuyển trái/phải để đỡ một quả bóng (Ball).  
Nhiệm vụ là làm bóng nảy lên, phá vỡ các viên gạch (Bricks) ở trên.  
Nếu bóng rơi xuống dưới paddle, người chơi mất 1 mạng.  
Trò chơi kết thúc khi hết gạch (thắng) hoặc hết mạng (thua).

#### 2.2.2. Các Tính năng

##### 1. Hệ thống Màn chơi (Level)
- Game có nhiều màn chơi (Level 1, 2, 3).  
- Dữ liệu màn chơi được lưu dưới dạng mảng 2 chiều (`int[][]`) trong các class Pattern (ví dụ: `PikachuPattern`, `CharmanderPattern`, `BulbasaurPattern`).

##### 2. Hệ thống Power-Up Đa hình (Polymorphism)
- Khi phá gạch, người chơi có cơ hội nhận được vật phẩm (Power-up) rơi xuống.  
- Một lớp trừu tượng `PowerUp` được định nghĩa, bắt buộc các power-up con phải có hàm `applyEffect()` và `removeEffect()`.  
- Mỗi power-up là một class riêng kế thừa từ `PowerUp`:
  - `ExpandPaddlePowerUp`: Phóng to thanh đỡ  
  - `FastBallPowerUp`: Tăng tốc độ bóng  
  - `ReverseControlsPowerUp`: Power-up "xấu", đảo ngược điều khiển (trái thành phải)  
- `GameManager` chỉ cần gọi `powerUp.applyEffect()`, thể hiện tính đa hình mà không cần biết đó là loại power-up gì.

##### 3. Bảng Xếp Hạng (Leaderboard) & Lưu trữ (Serialization)
- Khi trò chơi kết thúc (Game Over), một cửa sổ pop-up (Modal) sẽ hiện ra, yêu cầu người chơi nhập tên.  
- Dữ liệu (Tên, Điểm, Thời gian chơi) được đóng gói vào một đối tượng `GameRecord`.  
- `LeaderboardManager` (thiết kế theo Singleton Pattern) sẽ sử dụng `ObjectOutputStream` để serialize toàn bộ `List<GameRecord>` và lưu xuống file nhị phân (`leaderboard.ser`).  
- Khi mở Bảng Xếp Hạng, `LeaderboardManager` dùng `ObjectInputStream` để deserialize file, đọc lại danh sách, và hiển thị Top 10 hoặc Lịch sử chơi gần nhất (sử dụng Java Streams và Comparator).

##### 4. Tối ưu Trải nghiệm Người dùng (UX)
- **Đa luồng (Multithreading):** Khi chọn màn chơi, game sử dụng `javafx.concurrent.Task` để chạy logic “nặng” (tải ảnh, tạo gạch) trên một luồng nền.  
- **Tối ưu hiệu năng:** Tất cả tài nguyên (ảnh, âm thanh) được tải trước (Pre-load) một lần duy nhất lúc khởi động để đảm bảo vòng lặp game luôn mượt mà, không bị “lag”.

---

## 3. Kiến trúc Kỹ thuật (OOP) & Chức năng Class

### Package `base`
- **GameObject.java (Abstract):** Lớp cơ sở trừu tượng cho mọi đối tượng trong game. Cung cấp các thuộc tính cơ bản (x, y, width, height) và phương thức trừu tượng `update()`, `render()`.  
- **MovableObject.java (Abstract):** Kế thừa `GameObject`. Bổ sung thêm logic di chuyển (dx, dy) và hàm `move()`. Là lớp cha cho `Ball` và `Paddle`.

### Package `constants`
- **GameConfig.java:** Chứa toàn bộ các hằng số tĩnh (`static final`) của game như `WINDOW_WIDTH`, `PADDLE_SPEED`, `BALL_SIZE`...

### Package `core`
- **GameManager.java:** Bộ não logic chính của game. Quản lý trạng thái (điểm, mạng), xử lý va chạm, random PowerUp, v.v...  
- **LeaderboardManager.java:** Thiết kế theo Singleton Pattern. Chịu trách nhiệm serialization/deserialization danh sách `List<GameRecord>`.  
- **GameRecord.java:** Lớp chứa dữ liệu một lần chơi (`implements Serializable`) gồm tên, điểm, thời gian, timestamp.

### Package `graphics`
- **Main.java:** Entry point của ứng dụng JavaFX. Gọi `Application.launch()`.  
- **Menu.java:** Xử lý giao diện Menu chính, chọn “New Game”, “Leaderboard”, dùng `Task` để load màn chơi.  
- **GamePanel.java:** Lớp `Pane` chính chứa vòng lặp game (`AnimationTimer`), hiển thị và cập nhật đối tượng 60 lần/giây.  
- **Level1Panel.java / Level2Panel.java / Level3Panel.java:** Kế thừa `GamePanel`, chỉ định `Pattern` tương ứng.  
- **SaveScoreWindow.java:** Tạo cửa sổ pop-up nhập tên người chơi khi Game Over, lưu dữ liệu vào `LeaderboardManager`.  
- **Ball.java:** Kế thừa `MovableObject`. Cung cấp logic nảy (`reverseX()`, `reverseY()`) và va chạm với paddle (`hitPaddle()`).  
- **Paddle.java:** Kế thừa `MovableObject`. Cung cấp logic di chuyển `moveLeft()` và `moveRight()`.  
- **BrickDisplay.java:** Tải trước toàn bộ tài nguyên ảnh gạch, bóng, paddle, power-up để đảm bảo hiệu năng.  
- **SoundManager.java:** Quản lý và preload toàn bộ âm thanh (.wav, .mp3).  
- **PowerUp.java (Abstract):** Lớp cha trừu tượng định nghĩa `applyEffect(GameManager m)` và `removeEffect(GameManager m)`.  
- **ExpandPaddlePowerUp.java / FastBallPowerUp.java / ReverseControlsPowerUp.java:** Các lớp kế thừa thực thi hiệu ứng riêng.

### Package `Patterns`
- **PikachuPattern.java:** Chứa ma trận `int[][]` mô tả gạch hình Pikachu.  
- **CharmanderPattern.java:** Chứa ma trận `int[][]` mô tả gạch hình Charmander.  
- **BulbasaurPattern.java:** Chứa ma trận `int[][]` mô tả gạch hình Bulbasaur.
