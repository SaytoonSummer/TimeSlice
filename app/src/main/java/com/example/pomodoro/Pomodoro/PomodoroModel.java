package com.example.pomodoro.Pomodoro;

import android.os.Parcel;
import android.os.Parcelable;

public class PomodoroModel implements Parcelable {
    private String name;
    private int focus;
    private int breakTime;
    private int longBreak;
    private int rounds;

    private String documentId;

    public PomodoroModel() {
    }

    public PomodoroModel(String name, int focus, int breakTime, int longBreak, int rounds, String documentId) {
        this.name = name;
        this.focus = focus;
        this.breakTime = breakTime;
        this.longBreak = longBreak;
        this.rounds = rounds;
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public int getFocus() {
        return focus;
    }

    public int getBreakTime() {
        return breakTime;
    }

    public int getLongBreak() {
        return longBreak;
    }

    public int getRounds() {
        return rounds;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFocus(int focus) {
        this.focus = focus;
    }

    public void setBreakTime(int breakTime) {
        this.breakTime = breakTime;
    }

    public void setLongBreak(int longBreak) {
        this.longBreak = longBreak;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    protected PomodoroModel(Parcel in) {
        name = in.readString();
        focus = in.readInt();
        breakTime = in.readInt();
        longBreak = in.readInt();
        rounds = in.readInt();
        documentId = in.readString();
    }

    public static final Creator<PomodoroModel> CREATOR = new Creator<PomodoroModel>() {
        @Override
        public PomodoroModel createFromParcel(Parcel in) {
            return new PomodoroModel(in);
        }

        @Override
        public PomodoroModel[] newArray(int size) {
            return new PomodoroModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(focus);
        dest.writeInt(breakTime);
        dest.writeInt(longBreak);
        dest.writeInt(rounds);
        dest.writeString(documentId);
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}