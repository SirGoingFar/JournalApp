package com.eemf.sirgoingfar.journalapp.models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.eemf.sirgoingfar.journalapp.database.AppDatabase;
import com.eemf.sirgoingfar.journalapp.database.JournalEntry;

public class AddJournalViewModel extends ViewModel{

    private LiveData<JournalEntry> journal;

    public AddJournalViewModel(AppDatabase mDb, int mJournalId) {
        journal = mDb.journalDao().loadJournalById(mJournalId);
    }

    public LiveData<JournalEntry> getJournal() {
        return journal;
    }
}
