package graphics;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager {
    private MediaPlayer backgroundMusic;
    private AudioClip collisionSound;
    private AudioClip brickHitSound;
    private MediaPlayer powerUpSoundPlayer;
    private AudioClip dieSound;

    public SoundManager() {
        try {
            Media bgMusicMedia = new Media(getClass().getResource("/sounds/background_music.mp3").toExternalForm());
            backgroundMusic = new MediaPlayer(bgMusicMedia);

            collisionSound = new AudioClip(getClass().getResource("/sounds/vacham.wav").toExternalForm());
            brickHitSound = new AudioClip(getClass().getResource("/sounds/phavogach.wav").toExternalForm());

            Media powerUpMedia = new Media(getClass().getResource("/sounds/power_up.wav").toExternalForm());
            powerUpSoundPlayer = new MediaPlayer(powerUpMedia);

            dieSound = new AudioClip(getClass().getResource("/sounds/matmang.wav").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Không tìm thấy file âm thanh!");
        }
    }

    public void playBackgroundMusic() {
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusic.setVolume(0.5);
        backgroundMusic.play();
    }

    public void stopBackgroundMusic() {
        backgroundMusic.stop();
    }

    public void playCollisionSound() {
        collisionSound.play();
    }
    public void playBrickHitSound() {
        brickHitSound.setVolume(0.2);
        brickHitSound.play();
    }
    public void playPowerUpSound() {
        if (powerUpSoundPlayer != null) {
            powerUpSoundPlayer.stop();
            powerUpSoundPlayer.seek(powerUpSoundPlayer.getStartTime());
            powerUpSoundPlayer.play();
        }
    }
    public void playDieSound() { dieSound.play(); }
}
