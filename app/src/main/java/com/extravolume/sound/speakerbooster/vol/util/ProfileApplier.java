package com.extravolume.sound.speakerbooster.vol.util;

import android.widget.Toast;


import com.extravolume.sound.speakerbooster.vol.data.SoundProfile;
import com.speakerboooster.apps.libs.speakerboost;

import java.util.Map;

public class ProfileApplier {
    public static void applyProfile(speakerboost control, SoundProfile profile) {
        for (Map.Entry<Integer, Integer> nameAndVolume : profile.settings.entrySet()) {
            control.setVolumeLevel(nameAndVolume.getKey(), nameAndVolume.getValue());
        }
        Toast.makeText(control.getContext(), "Sounds profile " + profile.name + " applied", Toast.LENGTH_SHORT).show();
    }
}
