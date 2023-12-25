package com.extravolume.sound.speakerbooster.vol.util;


import com.extravolume.sound.speakerbooster.vol.data.SoundProfile;
import com.speakerboooster.apps.libs.speakerboost;

import java.util.ArrayList;
import java.util.List;

public class NotificationWidgetUpdateTracker {
    private int lastNotificationHashCode = Integer.MIN_VALUE;

    public void onNotificationShow(speakerboost control, List<Integer> profilesToShow, SoundProfile[] profiles) {
        lastNotificationHashCode = calculateHashCode(control, profilesToShow, profiles);
    }

    public boolean shouldShow(speakerboost control, List<Integer> profilesToShow, SoundProfile[] profiles) {
        return lastNotificationHashCode != calculateHashCode(control, profilesToShow, profiles);
    }


    private int calculateHashCode(speakerboost control, List<Integer> profilesToShow, SoundProfile[] profiles) {
        List<Object> result = new ArrayList<>();

        if (profilesToShow != null) {
            for (Integer id : profilesToShow) {
                result.add(id.toString() + " " + control.getLevel(id));
            }
        } else {
            result.add("no_volume_profiles");
        }

        for (SoundProfile soundProfile : profiles) {
            result.add(soundProfile);
        }

        System.out.println(result.hashCode());
        return result.hashCode();
    }
}
