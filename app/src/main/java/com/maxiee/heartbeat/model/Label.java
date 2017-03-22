package com.maxiee.heartbeat.model;

/**
 * Created by maxiee on 15-7-10.
 */
public class Label {
    private long mId;
    private String mLabel;

    public Label(long id, String label) {
        this.mId = id;
        this.mLabel = label;
    }

    public long getId() {return mId;}

    public String getLabel() {return mLabel;}
}
