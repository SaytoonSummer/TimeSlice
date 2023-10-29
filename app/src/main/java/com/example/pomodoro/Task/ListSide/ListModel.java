package com.example.pomodoro.Task.ListSide;

public class ListModel {

    private String listName;
    private String documentId;

    public ListModel() {
    }

    public ListModel(String listName, String documentId) {
        this.listName = listName;
        this.documentId = documentId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
