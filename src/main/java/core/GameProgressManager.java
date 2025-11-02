package core;

import java.util.prefs.Preferences;

/**
 * Lớp này dùng Java Preferences API (có sẵn)
 * để lưu và tải trạng thái game (level cao nhất đã mở khóa).
 * Nó hoạt động giống như registry (Windows) hoặc UserDefaults (macOS).
 */
public class GameProgressManager {

    // Khóa (key) để lưu giá trị. Giống như tên biến.
    private static final String HIGHEST_LEVEL_KEY = "highestLevel";

    // 1. Lấy ra "ngăn kéo" để lưu
    // Chúng ta dùng một "ngăn" riêng cho class này
    private static Preferences getPrefs() {
        return Preferences.userNodeForPackage(GameProgressManager.class);
    }

    /**
     * Lấy level cao nhất đã mở khóa.
     * @return Trả về 1 nếu là lần đầu chơi.
     */
    public static int loadHighestLevel() {
        // Lấy giá trị từ "ngăn kéo"
        // Nếu không tìm thấy (lần đầu chơi), nó sẽ trả về giá trị mặc định là 1.
        return getPrefs().getInt(HIGHEST_LEVEL_KEY, 1);
    }

    /**
     * Lưu level cao nhất MỚI.
     * Chỉ lưu nếu level mới này cao hơn level cũ.
     * @param levelToSave Level MỚI bạn muốn lưu (ví dụ: 2, 3...)
     */
    public static void saveHighestLevel(int levelToSave) {
        int currentMax = loadHighestLevel();

        // Chỉ lưu nếu level mới cao hơn cái đang lưu
        if (levelToSave > currentMax) {
            getPrefs().putInt(HIGHEST_LEVEL_KEY, levelToSave);
            System.out.println("ĐÃ LƯU TIẾN TRÌNH: Mở khóa Level " + levelToSave);
        }
    }
}