package com.extravolume.sound.speakerbooster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Build.VERSION;
import android.util.Log;

import com.extravolume.sound.speakerbooster.vol.MainActivity;

public class Setting {
  public static final int LOUDNESS_RANGE = 750;
  public static final int NOMINAL_RANGE_HIGH = 1500;
  private static final int PRIORITY = 87654325;
  private short bands;
  public int boostValue;


  private Equalizer f20eq;


  private LoudnessEnhancer f21le;
  private short rangeHigh;
  private short rangeLow;
  private boolean released;
  public boolean shape;

  @SuppressLint({"NewApi"})
  public Setting(Context context, boolean activeEqualizer) {
    this.shape = true;
    this.released = true;
    this.f20eq = null;
    this.f21le = null;
    this.f20eq = null;
    this.f21le = null;
    if (activeEqualizer) {
      if (19 <= VERSION.SDK_INT) {
        try {
          MainActivity.log("Trying LoudnessEnhancer");
          this.f21le = new LoudnessEnhancer(0);
          if (!activeEqualizer) {
            this.f21le.release();
            this.released = true;
          } else {
            this.released = false;
          }
          MainActivity.log("LE set");
          return;
        } catch (Exception e) {
          MainActivity.log("Error " + e);
          this.f21le = null;
        }
      }
      if (9 <= VERSION.SDK_INT) {
        try {
          this.f20eq = new Equalizer(PRIORITY, 0);
          this.bands = this.f20eq.getNumberOfBands();
          MainActivity.log("Set up equalizer, have " + this.bands + " bands");
          this.rangeLow = this.f20eq.getBandLevelRange()[0];
          this.rangeHigh = this.f20eq.getBandLevelRange()[1];
          MainActivity.log("range " + this.rangeLow + " " + this.rangeHigh);
          if (!activeEqualizer) {
            this.f20eq.release();
            this.released = true;
            return;
          }
          this.released = false;
        } catch (Exception e2) {
          MainActivity.log("Exception " + e2);
          this.f20eq = null;
        }
      }
    }
  }

  public void load(SharedPreferences pref) {
    this.boostValue = pref.getInt(Options.PREF_BOOST, 0);
    int maxBoost = (Options.getMaximumBoost(pref) * 1600) / 100;
    if (this.boostValue > maxBoost) {
      this.boostValue = maxBoost;
    }
    this.shape = pref.getBoolean(Options.PREF_SHAPE, true);
  }

  public void save(SharedPreferences pref) {
    Editor ed = pref.edit();
    ed.putInt(Options.PREF_BOOST, this.boostValue);
    ed.apply();
  }

  @SuppressLint({"NewApi"})
  public void setEqualizer() {
    boolean z;
    boolean z2 = true;
    if (this.f21le != null) {
      int gain = (this.boostValue * LOUDNESS_RANGE) / 100;
      Log.v("SpeakerBoost", "setting loudness boost to " + gain + " in state " + this.f21le.getEnabled() + " " + this.f21le.hasControl());
      try {
        boolean enabled = this.f21le.getEnabled();
        if (gain > 0) {
          z = true;
        } else {
          z = false;
        }
        if (enabled != z) {
          LoudnessEnhancer loudnessEnhancer = this.f21le;
          if (gain <= 0) {
            z2 = false;
          }
          loudnessEnhancer.setEnabled(z2);
        }
        this.f21le.setTargetGain(gain);
      } catch (Exception e) {
        Log.e("SpeakerBoost", "le " + e);
      }
    } else {
      setEqualizer(this.f20eq);
    }
  }

  private void setEqualizer(Equalizer e) {
    MainActivity.log("setEqualizer " + this.boostValue);
    if (e != null) {
      short v = (short) (((this.boostValue * this.rangeHigh) + LOUDNESS_RANGE) / 1500);
      if (v < 0) {
        v = 0;
      }
      if (v > this.rangeHigh) {
        v = this.rangeHigh;
      }
      if (v != 0) {
        e.setEnabled(true);
        for (short i = 0; i < this.bands; i = (short) (i + 1)) {
          short adj = v;
          if (this.shape) {
            int hz = e.getCenterFreq(i) / 1000;
            if (hz < 150) {
              adj = 0;
            } else if (hz < 250) {
              adj = (short) (v / 2);
            } else if (hz > 8000) {
              adj = (short) ((v * 3) / 4);
            }
          }
          MainActivity.log("boost " + i + " (" + (this.f20eq.getCenterFreq(i) / 1000) + "hz) to " + adj);
          MainActivity.log("previous value " + this.f20eq.getBandLevel(i));
          try {
            e.setBandLevel(i, adj);
          } catch (Exception exc) {
            MainActivity.log("Error " + exc);
          }
        }
        return;
      }
      e.setEnabled(false);
    }
  }

  public void setAll() {
    setEqualizer();
  }

  public boolean haveEqualizer() {
    return (this.f21le == null && this.f20eq == null) ? false : true;
  }

  @SuppressLint({"NewApi"})
  public void destroyEqualizer() {
    disableEqualizer();
    if (this.f21le != null) {
      MainActivity.log("Destroying le");
      this.f21le.release();
      this.released = true;
      this.f21le = null;
    }
    if (this.f20eq != null) {
      MainActivity.log("Destroying equalizer");
      this.f20eq.release();
      this.released = true;
      this.f20eq = null;
    }
  }

  @SuppressLint({"NewApi"})
  public void disableEqualizer() {
    if (this.f21le != null && !this.released) {
      MainActivity.log("Closing loudnessenhancer");
      this.f21le.setEnabled(false);
    } else if (this.f20eq != null && !this.released) {
      MainActivity.log("Closing equalizer");
      this.f20eq.setEnabled(false);
    }
  }

  public boolean isEqualizerActive() {
    return this.boostValue > 0;
  }

  public boolean needService() {
    return isEqualizerActive();
  }

  public boolean somethingOn() {
    return isEqualizerActive();
  }

  public String describe() {
    if (!somethingOn()) {
      return "Speaker Booster is off";
    }
    String[] list = new String[1];
    int count = 0;
    if (isEqualizerActive()) {
      int count2 = 0 + 1;
      list[0] = "\tBoost is on";
      count = count2;
    }
    String out = "";
    for (int i = 0; i < count; i++) {
      out = out + list[i];
      if (i + 1 < count) {
        out = out + ", ";
      }
    }
    return out;
  }
}
