package com.extravolume.sound.speakerbooster.vol.model;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.extravolume.sound.speakerbooster.vol.data.Settings;


public class SettingsStorage {
    private static final String KEY_NOTIFICATION_WIDGET = "NOTIFICATION_WIDGET";
    private static final String KEY_EXTENDED_VOLUME_SETTINGS = "EXTENDED_VOLUME_SETTINGS";
    private static final String KEY_DARK_THEME = "DARK_THEME";
    private static final String KEY_VOLUME_TYPES_IDS = "KEY_VOLUME_TYPES_IDS";
    private static final String KEY_SHOW_PROFILE_IN_NOTIFICATION = "KEY_SHOW_PROFILE_IN_NOTIFICATION";
    private final SharedPreferences preferences;

    public SettingsStorage(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    private static String serializeIds(Integer[] ids) {
        return TextUtils.join(",", ids);
    }

    private static Integer[] deserializeIds(String string) {

        String[] r = TextUtils.split(string, ",");

        Integer[] result = new Integer[r.length];
        for (int i = 0; i < r.length; i++) {
            result[i] = Integer.valueOf(r[i]);
        }
        return result;
    }

    public void save(Settings settings) {
        preferences.edit()
                .putBoolean(KEY_NOTIFICATION_WIDGET, settings.isNotificationWidgetEnabled)
                .putBoolean(KEY_DARK_THEME, settings.isDarkThemeEnabled)
                .putBoolean(KEY_EXTENDED_VOLUME_SETTINGS, settings.isExtendedVolumeSettingsEnabled)
                .putBoolean(KEY_SHOW_PROFILE_IN_NOTIFICATION, settings.showProfilesInNotification)
                .putString(KEY_VOLUME_TYPES_IDS, serializeIds(settings.volumeTypesToShow))
                .apply();
    }

    private static Settings defaultSettings = new Settings();

    public Settings settings() {
        return new Settings(
                preferences.getBoolean(KEY_DARK_THEME, defaultSettings.isDarkThemeEnabled),
                preferences.getBoolean(KEY_EXTENDED_VOLUME_SETTINGS, defaultSettings.isExtendedVolumeSettingsEnabled),
                preferences.getBoolean(KEY_NOTIFICATION_WIDGET, defaultSettings.isNotificationWidgetEnabled),
                preferences.getBoolean(KEY_SHOW_PROFILE_IN_NOTIFICATION, defaultSettings.showProfilesInNotification),
                deserializeIds(preferences.getString(KEY_VOLUME_TYPES_IDS, serializeIds(defaultSettings.volumeTypesToShow)))
        );
    }

}
