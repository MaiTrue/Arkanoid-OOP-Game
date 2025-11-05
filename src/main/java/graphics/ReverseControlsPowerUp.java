package graphics;

import core.GameManager;
import javafx.scene.image.Image;

public class ReverseControlsPowerUp extends PowerUp {

    public ReverseControlsPowerUp(double x, double y, Image image) {

        // Gọi lớp cha, đặt tên là "Reverse", thời gian 7 giây.
        // DÙNG CHÍNH THAM SỐ 'image' TRUYỀN VÀO TỪ GAMEMANAGER
        super(x, y, "Reverse", 3, image);

        // *** XÓA TẤT CẢ CODE GHI ĐÈ ẢNH Ở ĐÂY ***
        // (Không còn logic new Image(...) hay try/catch nữa)
    }

    /**
     * HÀM KÍCH HOẠT: Bật chế độ điều khiển ngược
     */
    @Override
    public void applyEffect(GameManager manager) {
        manager.setControlsReversed(true);
    }

    /**
     * HÀM HỦY: Trả về điều khiển bình thường
     */
    @Override
    public void removeEffect(GameManager manager) {
        manager.setControlsReversed(false);
    }
}