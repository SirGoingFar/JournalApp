package com.eemf.sirgoingfar.journalapp.firebase_transaction.task;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Journal {

    private String journalTitle;
    private String journalContent;
    private @ServerTimestamp Date created_at;
    private @ServerTimestamp Date updated_at;
    private int edit_status;
    private String user_id;
    private String journal_id;

    public Journal() {
    }

    public Journal(String journalTitle, String journalContent, Date created_at,
                   Date updated_at, int edit_status, String user_id, String journal_id) {
        this.journalTitle = journalTitle;
        this.journalContent = journalContent;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.edit_status = edit_status;
        this.user_id = user_id;
        this.journal_id = journal_id;
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
        return created_at;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(Date updated_at) {
        this.updated_at = updated_at;
    }

    public int getEditStatus() {
        return edit_status;
    }

    public void setEditStatus(int edit_status) {
        this.edit_status = edit_status;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getJournalId() {
        return journal_id;
    }

    public void setJournalId(String journal_id) {
        this.journal_id = journal_id;
    }
}
