package com.eemf.sirgoingfar.journalapp.models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.eemf.sirgoingfar.journalapp.database.AppDatabase;
import com.eemf.sirgoingfar.journalapp.database.JournalEntry;

import java.util.List;


public class CatalogViewModel extends AndroidViewModel {

    private LiveData<List<JournalEntry>> journals;

    public CatalogViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(this.getApplication());
        journals = db.journalDao().loadAllJournal();
    }

    public LiveData<List<JournalEntry>> getJournals(){
        return journals;
    }
}
