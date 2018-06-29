package com.eemf.sirgoingfar.journalapp.models;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.eemf.sirgoingfar.journalapp.database.AppDatabase;


public class AddJournalViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AppDatabase mDb;
    private final int mJournalId;

    public AddJournalViewModelFactory(AppDatabase mDb, int mJournalId) {
        this.mDb = mDb;
        this.mJournalId = mJournalId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass){
        return (T) new AddJournalViewModel(mDb, mJournalId);
    }
}
