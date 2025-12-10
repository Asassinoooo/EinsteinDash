package com.EinsteinDash.frontend.audio;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

/**
 * AudioManager - Singleton manager untuk semua audio dalam game.
 * Menangani music tracks, volume control, dan playback state.
 */
public class AudioManager implements Disposable {

    // === CONSTANTS ===
    private static final String TAG = "AudioManager";
    private static final float DEFAULT_VOLUME = 0.5f;

    // === MUSIC STATE ===
    private final Map<Integer, Music> musicTracks;  // Map audioTrackId -> Music
    private Music currentMusic;                      // Music yang sedang diputar
    private int currentTrackId = -1;                 // Track ID yang sedang aktif
    private float musicVolume;                       // Volume musik (0.0 - 1.0)

    // ==================== CONSTRUCTOR ====================

    public AudioManager() {
        this.musicTracks = new HashMap<>();
        this.musicVolume = DEFAULT_VOLUME;
        this.currentMusic = null;
        this.currentTrackId = -1;
    }

    // ==================== INITIALIZATION ====================

    /**
     * Load semua music tracks ke memory.
     * Panggil method ini saat game create().
     */
    public void loadAllTracks() {
        Gdx.app.log(TAG, "Loading music tracks...");

        // Daftarkan music berdasarkan audioTrackId dari database
        // Format: loadTrack(audioTrackId, "music/nama_file.mp3")
        loadTrack(1001, "music/We Are Charlie Kirk.mp3");

        // Tambahkan track lain sesuai kebutuhan:
        // loadTrack(1002, "music/track_2.mp3");
        // loadTrack(1003, "music/track_3.mp3");

        Gdx.app.log(TAG, "Loaded " + musicTracks.size() + " tracks");
    }

    /**
     * Load single music track ke memory.
     */
    public void loadTrack(int trackId, String filePath) {
        try {
            if (Gdx.files.internal(filePath).exists()) {
                Music music = Gdx.audio.newMusic(Gdx.files.internal(filePath));
                music.setLooping(true);
                musicTracks.put(trackId, music);
                Gdx.app.log(TAG, "Loaded track " + trackId + ": " + filePath);
            } else {
                Gdx.app.log(TAG, "File not found: " + filePath);
            }
        } catch (Exception e) {
            Gdx.app.error(TAG, "Failed to load: " + filePath, e);
        }
    }

    // ==================== PLAYBACK CONTROL ====================

    /**
     * Play music dari awal (posisi 0).
     * Selalu restart meskipun track yang sama sedang diputar.
     * Gunakan untuk: memulai level, restart level, player mati.
     */
    public void playFromStart(int trackId) {
        // Stop music sebelumnya
        stop();

        // Play track dari awal
        Music music = musicTracks.get(trackId);
        if (music != null) {
            music.setPosition(0);  // Reset ke posisi awal
            music.setVolume(musicVolume);
            music.play();
            currentMusic = music;
            currentTrackId = trackId;
            Gdx.app.log(TAG, "Playing track " + trackId + " from start");
        } else {
            Gdx.app.log(TAG, "Track " + trackId + " not found!");
        }
    }

    /**
     * Play music dengan seamless transition.
     * Jika track yang sama sudah diputar, tidak akan restart.
     * Gunakan untuk: transisi antar screen dengan music yang sama.
     */
    public void play(int trackId) {
        // Jangan restart jika track sama dan sedang playing
        if (trackId == currentTrackId && currentMusic != null && currentMusic.isPlaying()) {
            return;
        }

        // Track berbeda, play dari awal
        playFromStart(trackId);
    }

    /**
     * Stop music yang sedang diputar.
     */
    public void stop() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
        currentMusic = null;
        currentTrackId = -1;
    }

    /**
     * Pause music yang sedang diputar.
     * Gunakan saat game pause.
     */
    public void pause() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
            Gdx.app.log(TAG, "Music paused");
        }
    }

    /**
     * Resume music yang sedang di-pause.
     * Gunakan setelah unpause game.
     */
    public void resume() {
        if (currentMusic != null && !currentMusic.isPlaying()) {
            currentMusic.play();
            Gdx.app.log(TAG, "Music resumed");
        }
    }

    // ==================== VOLUME CONTROL ====================

    /**
     * Get volume musik saat ini.
     */
    public float getVolume() {
        return musicVolume;
    }

    /**
     * Set volume musik.
     */
    public void setVolume(float volume) {
        // Clamp volume ke range valid
        this.musicVolume = Math.max(0f, Math.min(1f, volume));

        // Update volume music yang sedang diputar
        if (currentMusic != null) {
            currentMusic.setVolume(this.musicVolume);
        }

        Gdx.app.log(TAG, "Volume set to " + this.musicVolume);
    }

    // ==================== STATE QUERY ====================

    /**
     * Cek apakah ada music yang sedang diputar.
     */
    public boolean isPlaying() {
        return currentMusic != null && currentMusic.isPlaying();
    }

    /**
     * Get ID track yang sedang diputar.
     */
    public int getCurrentTrackId() {
        return currentTrackId;
    }

    /**
     * Cek apakah track tertentu tersedia.
     */
    public boolean hasTrack(int trackId) {
        return musicTracks.containsKey(trackId);
    }

    // ==================== CLEANUP ====================

    /**
     * Dispose semua resources.
     * Panggil saat game dispose().
     */
    @Override
    public void dispose() {
        Gdx.app.log(TAG, "Disposing audio resources...");

        // Stop music yang sedang diputar
        stop();

        // Dispose semua music tracks
        for (Music music : musicTracks.values()) {
            if (music != null) {
                music.dispose();
            }
        }
        musicTracks.clear();

        Gdx.app.log(TAG, "Audio resources disposed");
    }
}
