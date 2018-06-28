package com.eemf.sirgoingfar.journalapp.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class JournalEntity {
    @PrimaryKey(autoGenerate = true)
    private int _id;
    private String journalTitle;
    private String journalContent;
    private Date createdAt;
    private Date updatedAt;
    private int editStatus;

    public JournalEntity(String journalTitle, String journalContent,
                         Date createdAt, Date updatedAt, int editStatus) {
        this.journalTitle = journalTitle;
        this.journalContent = journalContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.editStatus = editStatus;
    }

    @Ignore
    public JournalEntity(int _id, String journalTitle, String journalContent,
                         Date createdAt, Date updatedAt, int editStatus) {
        this._id = _id;
        this.journalTitle = journalTitle;
        this.journalContent = journalContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.editStatus = editStatus;
    }

    public String getJournalTitle() {
        return journalTitle;
    }

    public void setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
    }

    public String getJournalContent() {
        return journalContent;
    }

    public void setJournalContent(String journalContent) {
        this.journalContent = journalContent;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getEditStatus() {
        return editStatus;
    }

    public void setEditStatus(int editStatus) {
        this.editStatus = editStatus;
    }
}
