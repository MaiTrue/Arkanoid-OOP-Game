package core;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

// Bổ sung implements Serializable
public class GameRecord implements Serializable, Comparable<GameRecord> {
    // Version UID cho Serialization
    private static final long serialVersionUID = 1L;

    private final String playerName;
    private final int score;
    private final long timeElapsedSeconds;
    // LƯU TRỮ THỜI ĐIỂM DƯỚI DẠNG SỐ (EPOCH MILLIS) - AN TOÀN CHO FILE I/O VÀ SERIALIZATION
    private final long timestampMillis;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.systemDefault());

    // Constructor MỚI (Dùng cho GamePanel để lưu kết quả)
    public GameRecord(String playerName, int score, long timeElapsedSeconds) {
        this(playerName, score, timeElapsedSeconds, Instant.now().toEpochMilli()); // Gán thời điểm hiện tại
    }

    // Constructor ĐẦY ĐỦ (Dùng khi đọc lại từ file)
    public GameRecord(String playerName, int score, long timeElapsedSeconds, long timestampMillis) {
        this.playerName = playerName;
        this.score = score;
        this.timeElapsedSeconds = timeElapsedSeconds;
        this.timestampMillis = timestampMillis;
    }

    // --- GETTERS ---
    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public long getTimeElapsedSeconds() {
        return timeElapsedSeconds;
    }

    // GETTER CHO THỜI ĐIỂM DƯỚI DẠNG SỐ
    public long getTimestampMillis() {
        return timestampMillis;
    }

    /**
     * Trả về chuỗi định dạng HH:mm:ss cho THỜI GIAN CHƠI (Duration)
     */
    public String getFormattedTime() {
        long hours = timeElapsedSeconds / 3600;
        long minutes = (timeElapsedSeconds % 3600) / 60;
        long seconds = timeElapsedSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Trả về chuỗi định dạng dd/MM/yyyy HH:mm:ss cho THỜI ĐIỂM ghi nhận.
     */
    public String getFormattedTimestamp() {
        return FORMATTER.format(Instant.ofEpochMilli(timestampMillis));
    }

    // So sánh để sắp xếp giảm dần theo điểm
    @Override
    public int compareTo(GameRecord other) {
        return Integer.compare(other.score, this.score);
    }

    // LOẠI BỎ toFileString/fromFileString nếu sử dụng LeaderboardManager với Serialization
}