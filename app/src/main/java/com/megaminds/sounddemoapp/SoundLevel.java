package com.megaminds.sounddemoapp;

/**
 * Created by santosh on 3/9/15.
 */
public class SoundLevel {

    long rowId;
    long date;
    int quietLevel,groupLevel,noiseLevel;

    public SoundLevel(long rowId, long date, int quietLevel, int groupLevel, int noiseLevel) {
        this.rowId = rowId;
        this.date = date;
        this.quietLevel = quietLevel;
        this.groupLevel = groupLevel;
        this.noiseLevel = noiseLevel;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof SoundLevel){
            SoundLevel currentSoundLevel=(SoundLevel)o;
            return rowId==currentSoundLevel.rowId;
        }
        return super.equals(o);
    }


}
