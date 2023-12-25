package com.extravolume.sound.speakerbooster.vol.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;


import com.extravolume.sound.speakerbooster.R;
import com.extravolume.sound.speakerbooster.vol.MainActivity;
import com.extravolume.sound.speakerbooster.vol.data.SoundProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TargetApi(Build.VERSION_CODES.O)
public class DynamicShortcutManager {

    private static String profileToShortcutId(SoundProfile profile) {
        return "profile_" + profile.id.toString();
    }

    private static ShortcutInfo createShortcutInfo(Activity activity, SoundProfile profile) {
        return new ShortcutInfo.Builder(activity.getApplicationContext(), profileToShortcutId(profile))
                .setIntent(MainActivity.createOpenProfileIntent(activity, profile))
                .setShortLabel(profile.name)
                .setLongLabel(profile.name)
                .setDisabledMessage("Login to open this")
                .setIcon(Icon.createWithResource(activity.getApplicationContext(), R.mipmap.volume_boster))
                .build();
    }

    public static boolean isPinnedShortcutSupported(Activity activity) {
        ShortcutManager shortcutManager = activity.getSystemService(ShortcutManager.class);
        return shortcutManager.isRequestPinShortcutSupported();
    }

    public static boolean installPinnedShortcut(Activity activity, SoundProfile soundProfile) {
        ShortcutInfo info = createShortcutInfo(activity, soundProfile);
        ShortcutManager shortcutManager = activity.getSystemService(ShortcutManager.class);
        return shortcutManager.requestPinShortcut(info, null);
    }

    public static void setShortcuts(Activity activity, SoundProfile[] soundProfiles) {
        final List<ShortcutInfo> shortcutInfos = new ArrayList<>();
        for (SoundProfile soundProfile : soundProfiles) {
            if (!soundProfile.name.isEmpty()) {
                shortcutInfos.add(createShortcutInfo(activity, soundProfile));
            }
        }
        ShortcutManager shortcutManager = activity.getSystemService(ShortcutManager.class);
        if (shortcutManager.getMaxShortcutCountPerActivity() < shortcutInfos.size()) {
            int last = shortcutInfos.size() - 1;
            int first = last - shortcutManager.getMaxShortcutCountPerActivity();
            shortcutManager.setDynamicShortcuts(shortcutInfos.subList(first, last));
        } else {
            shortcutManager.setDynamicShortcuts(shortcutInfos);
        }

    }

    public static void removeShortcut(Activity activity, SoundProfile soundProfile) {
        ShortcutManager shortcutManager = activity.getSystemService(ShortcutManager.class);
        shortcutManager.removeDynamicShortcuts(Collections.singletonList(profileToShortcutId(soundProfile)));
    }
}
