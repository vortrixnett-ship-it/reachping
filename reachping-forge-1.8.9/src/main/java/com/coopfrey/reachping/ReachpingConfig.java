package com.coopfrey.reachping;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ReachpingConfig {
    public static final String DEFAULT_SOUND = "note.pling";
    public static final String DEFAULT_FAIL_SOUND = "note.bd";
    public static final double MAX_DISTANCE = 6.0;

    public static boolean enabled = true;          // long reach sound on/off
    public static double distance = 3.0;           // play sound past this many blocks
    public static boolean onlyPlayers = false;
    public static double volume = 1.0;
    public static String soundId = DEFAULT_SOUND;
    public static boolean failSoundEnabled = false;
    public static String failSoundId = DEFAULT_FAIL_SOUND;

    private static Configuration cfg;
    private static final String C = "general";

    public static void init(File file) {
        cfg = new Configuration(file);
        load();
    }

    public static void load() {
        cfg.load();
        enabled = cfg.getBoolean("long_reach_sound", C, true, "Play a sound when you hit something far away");
        distance = clampDistance(cfg.get(C, "long_reach_distance", 3.0, "Distance in blocks (0 - 6)").getDouble());
        onlyPlayers = cfg.getBoolean("only_players", C, false, "Only react to players");
        volume = clampVolume(cfg.get(C, "sound_volume", 1.0, "Volume 0.0 - 1.0").getDouble());
        soundId = nonBlank(cfg.getString("sound_event", C, DEFAULT_SOUND, "1.8.9 sound name, e.g. note.pling"), DEFAULT_SOUND);
        failSoundEnabled = cfg.getBoolean("fail_sound_enabled", C, false, "Play a sound on hits within the distance");
        failSoundId = nonBlank(cfg.getString("fail_sound_event", C, DEFAULT_FAIL_SOUND, "1.8.9 sound name, e.g. note.bd"), DEFAULT_FAIL_SOUND);
        if (cfg.hasChanged()) cfg.save();
    }

    public static void save() {
        cfg.get(C, "long_reach_sound", true).set(enabled);
        cfg.get(C, "long_reach_distance", 3.0).set(distance);
        cfg.get(C, "only_players", false).set(onlyPlayers);
        cfg.get(C, "sound_volume", 1.0).set(volume);
        cfg.get(C, "sound_event", DEFAULT_SOUND).set(soundId);
        cfg.get(C, "fail_sound_enabled", false).set(failSoundEnabled);
        cfg.get(C, "fail_sound_event", DEFAULT_FAIL_SOUND).set(failSoundId);
        cfg.save();
    }

    public static void reset() {
        enabled = true;
        distance = 3.0;
        onlyPlayers = false;
        volume = 1.0;
        soundId = DEFAULT_SOUND;
        failSoundEnabled = false;
        failSoundId = DEFAULT_FAIL_SOUND;
        save();
    }

    public static double clampDistance(double d) {
        return Math.max(0.0, Math.min(MAX_DISTANCE, d));
    }

    public static double clampVolume(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    public static String nonBlank(String s, String def) {
        return (s == null || s.trim().isEmpty()) ? def : s.trim();
    }
}
