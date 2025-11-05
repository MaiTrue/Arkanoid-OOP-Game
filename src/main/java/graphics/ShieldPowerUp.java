package graphics;

import core.GameManager;
import javafx.scene.image.Image;

/**
 * ShieldPowerUp: Cung cấp một lớp chắn ở đáy màn hình, chỉ đỡ bóng 1 lần.
 */
public class ShieldPowerUp extends PowerUp {
    // Sử dụng tên loại cố định để GameManager có thể nhận biết và ngăn chặn cộng dồn
    private static final String POWERUP_TYPE = "Shield";

    // Vì lá chắn không hết hạn theo thời gian, duration có thể là 0 hoặc một giá trị lớn.
    // GameManager sẽ quản lý việc loại bỏ nó khi nó được sử dụng.
    private static final double NO_TIME_LIMIT = 0.0;

    public ShieldPowerUp(double x, double y, Image image) {
        // Kế thừa constructor của PowerUp:
        // x, y: vị trí rơi
        // type: "Shield"
        // duration: 0.0 (không giới hạn thời gian)
        // image: hình ảnh shield.png
        super(x, y, POWERUP_TYPE, NO_TIME_LIMIT, image);
    }

    /**
     * Áp dụng hiệu ứng: Kích hoạt lá chắn nếu nó chưa được kích hoạt.
     * Ngăn chặn cộng dồn: Logic kiểm tra (if (!manager.hasShield())) phải nằm trong GameManager
     * hoặc được đảm bảo bằng cách GameManager chỉ gọi applyEffect nếu không có Shield đang hoạt động.
     */
    @Override
    public void applyEffect(GameManager manager) {
        // Trong trường hợp này, chúng ta sẽ để GameManager kiểm tra
        // để đảm bảo không cộng dồn, nhưng lớp PowerUp vẫn gọi phương thức kích hoạt.

        // GameManager sẽ kiểm tra: if (!manager.isPowerUpActive("Shield")) { manager.setShieldActive(true); }
        manager.setShieldActive(true);
    }

    /**
     * Gỡ bỏ hiệu ứng: Tắt lá chắn.
     * Phương thức này sẽ được gọi bởi GameManager sau khi lá chắn đỡ bóng.
     */
    @Override
    public void removeEffect(GameManager manager) {
        manager.setShieldActive(false);
    }
}