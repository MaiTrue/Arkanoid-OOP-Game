package core;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class GameRecord implements Serializable, Comparable<GameRecord> {
    private static final long serialVersionUID = 1L;
    private final String playerName;
    private final int score;
    private final long timeElapsedSeconds;
    private final long timestampMillis;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.systemDefault());
    public GameRecord(String playerName, int score, long timeElapsedSeconds) {
        this(playerName, score, timeElapsedSeconds, Instant.now().toEpochMilli());
    }

    // Constructor ĐẦY ĐỦ (Dùng khi đọc lại từ file)
    public GameRecord(String playerName, int score, long timeElapsedSeconds, long timestampMillis) {
        this.playerName = playerName;
        this.score = score;
        this.timeElapsedSeconds = timeElapsedSeconds;
        this.timestampMillis = timestampMillis;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public long getTimeElapsedSeconds() {
        return timeElapsedSeconds;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public String getFormattedTime() {
        long hours = timeElapsedSeconds / 3600;
        long minutes = (timeElapsedSeconds % 3600) / 60;
        long seconds = timeElapsedSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String getFormattedTimestamp() {
        return FORMATTER.format(Instant.ofEpochMilli(timestampMillis));
    }
    @Override
    public int compareTo(GameRecord other) {
        return Integer.compare(other.score, this.score);
    }
}