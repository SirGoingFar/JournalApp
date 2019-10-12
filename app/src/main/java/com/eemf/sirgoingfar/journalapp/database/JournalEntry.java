package com.eemf.sirgoingfar.journalapp.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

@Entity(tableName = "journal")
public class JournalEntry implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "journal_title")
    private String journalTitle;

    @ColumnInfo(name = "journal_content")
    private String journalContent;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @ColumnInfo(name = "edit_status")
    private int editStatus;

    @ColumnInfo(name = "journal_id")
    private String journalId;

    @Ignore
    public JournalEntry(String journalTitle, String journalContent,
                        Date createdAt, Date updatedAt, int editStatus, String journalId) {
        this.journalTitle = journalTitle;
        this.journalContent = journalContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.editStatus = editStatus;
        this.journalId = journalId;
    }

    public JournalEntry(int id, String journalTitle, String journalContent,
                        Date createdAt, Date updatedAt, int editStatus, String journalId) {
        this.id = id;
        this.journalTitle = journalTitle;
        this.journalContent = journalContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.editStatus = editStatus;
        this.journalId = journalId;

    }

    protected JournalEntry(Parcel in) {
        id = in.readInt();
        journalTitle = in.readString();
        journalContent = in.readString();
        editStatus = in.readInt();
        journalId = in.readString();
    }

    public static final Creator<JournalEntry> CREATOR = new Creator<JournalEntry>() {
        @Override
        public JournalEntry createFromParcel(Parcel in) {
            return new JournalEntry(in);
        }

        @Override
        public JournalEntry[] newArray(int size) {
            return new JournalEntry[size];
        }
    };

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEditStatus() {
        return editStatus;
    }

    public void setEditStatus(int editStatus) {
        this.editStatus = editStatus;
    }

    public String getJournalId() {
        return journalId;
    }

    public void setJournalId(String journalId) {
        this.journalId = journalId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(journalTitle);
        dest.writeString(journalContent);
        dest.writeInt(editStatus);
        dest.writeString(journalId);
    }
}
