package com.eemf.sirgoingfar.journalapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface JournalDao {

    @Query("SELECT * FROM journal ORDER BY created_at DESC")
    LiveData<List<JournalEntry>> loadAllJournal();

    @Query("DELETE FROM journal")
    void deleteAllJournals();

    @Query("SELECT * FROM journal WHERE id = :id")
    LiveData<JournalEntry> loadJournalById(int id);

    @Insert
    void insertJournal(JournalEntry journal);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateJournal(JournalEntry journal);

    @Delete
    void deleteJournalById(JournalEntry journal);
}
