package com.example.pomodoro.Task.TaskSide;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskModel implements Parcelable {
    private String taskName;
    private String documentId;
    private String description;
    private String priority; // Cambiado de int a String
    private String note;
    private String tag;

    private String listId;

    public TaskModel() {
    }

    public TaskModel(String taskName, String documentId, String description, String priority, String note, String tag) {
        this.taskName = taskName;
        this.documentId = documentId;
        this.description = description;
        this.priority = priority;
        this.note = note;
        this.tag = tag;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public String getNote() {
        return note;
    }

    public String getTag() {
        return tag;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    // Parcelable implementation
    protected TaskModel(Parcel in) {
        taskName = in.readString();
        documentId = in.readString();
        description = in.readString();
        priority = in.readString(); // Cambiado de readInt a readString
        note = in.readString();
        tag = in.readString();
    }

    public static final Creator<TaskModel> CREATOR = new Creator<TaskModel>() {
        @Override
        public TaskModel createFromParcel(Parcel in) {
            return new TaskModel(in);
        }

        @Override
        public TaskModel[] newArray(int size) {
            return new TaskModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(taskName);
        dest.writeString(documentId);
        dest.writeString(description);
        dest.writeString(priority); // Cambiado de writeInt a writeString
        dest.writeString(note);
        dest.writeString(tag);
    }
}
