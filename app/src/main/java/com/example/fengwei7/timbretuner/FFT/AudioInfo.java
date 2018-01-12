package com.example.fengwei7.timbretuner.FFT;


import io.realm.RealmList;
import io.realm.RealmObject;

public class AudioInfo extends RealmObject {
    String mPath;
    RealmList<AudioMagnitude> mAudioMagnitudes=new RealmList<>();

    public RealmList<AudioMagnitude> getAudioMagnitudes() {
        return mAudioMagnitudes;
    }

    public void setAudioMagnitudes(RealmList<AudioMagnitude> audioMagnitudes) {
        mAudioMagnitudes = audioMagnitudes;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }
}
