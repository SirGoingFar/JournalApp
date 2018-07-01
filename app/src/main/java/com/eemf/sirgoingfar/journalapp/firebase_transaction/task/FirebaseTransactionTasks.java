package com.eemf.sirgoingfar.journalapp.firebase_transaction.task;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.eemf.sirgoingfar.journalapp.activities.CatalogActivity;
import com.eemf.sirgoingfar.journalapp.database.AppDatabase;
import com.eemf.sirgoingfar.journalapp.database.AppExecutors;
import com.eemf.sirgoingfar.journalapp.database.JournalEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseTransactionTasks {

    private final String COLLECTION_NAME = "user_journal";
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private String userId;
    private boolean isAppLaunch;

    //CRUD actions specifiers
    public static final String INSERT_DOCUMENT = "insert_firebase";
    public static final String QUERY_DOCUMENT = "query_firebase";
    public static final String UPDATE_DOCUMENT = "update_firebase";
    public static final String DELETE_DOCUMENT = "delete_firebase";

    public static final String START_CATALOG_ACTIVITY = "start_catalog_activity";

    //AppExecutor and AppDatabase instances
    private AppExecutors mExecutor;
    private AppDatabase mDb;

    public FirebaseTransactionTasks() {
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
    }

    public void execute(Context context, String action, JournalEntry journal){
        //instantiate accessors and threaders
        mExecutor = AppExecutors.getInstance();
        mDb = AppDatabase.getInstance(context);

        switch (action){
            case START_CATALOG_ACTIVITY:
                queryJournalList(context);
                isAppLaunch = true;
                break;

            case QUERY_DOCUMENT:
                queryJournalList(context);
                isAppLaunch = false;
                break;

            case INSERT_DOCUMENT:
                createJournal(journal);
                break;

            case UPDATE_DOCUMENT:
                updateJournal(context, journal);
                break;

            case DELETE_DOCUMENT:
                deleteJournal(journal);
                break;
        }
    }

    private void createJournal(final JournalEntry journal){

        //make a document reference
        DocumentReference newJournalReference = mFirestore.collection(COLLECTION_NAME).document();

        //create a Firebase compatible Journal object using Map
        Map<String, Object> newJournal = new HashMap<>();
        newJournal.put("journalTitle", journal.getJournalTitle());
        newJournal.put("journalContent", journal.getJournalContent());
        newJournal.put("editStatus", journal.getEditStatus());
        newJournal.put("createdAt", FieldValue.serverTimestamp());
        newJournal.put("updatedAt", FieldValue.serverTimestamp());
        newJournal.put("journalId", newJournalReference.getId());
        newJournal.put("userId", userId);

        final String journalId = newJournalReference.getId();

        //insert the journal into the local db
       mExecutor.diskIO().execute(new Runnable() {
           @Override
           public void run() {
               mDb.journalDao().insertJournal( new JournalEntry(
                       journal.getJournalTitle(),
                       journal.getJournalContent(),
                       new Date(),
                       new Date(),
                       1,
                       journalId
               ));
           }
       });

        //save to Firestore
        newJournalReference.set(newJournal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    createJournal(journal);

                }
            }
        });
    }

    private void queryJournalList(final Context context){
        final List<JournalEntry> journalList = new ArrayList<>();

        CollectionReference journalConnectionRef = mFirestore.collection(COLLECTION_NAME);

        Query fetchJournalQuery = journalConnectionRef
                .whereEqualTo("userId",userId)
                .orderBy("createdAt", Query.Direction.DESCENDING);

        fetchJournalQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot document : task.getResult()){

                        Journal newJournal = document.toObject(Journal.class);
                        journalList.add(new JournalEntry(
                                newJournal.getJournalTitle(),
                                newJournal.getJournalContent(),
                                newJournal.getCreatedAt(),
                                newJournal.getUpdatedAt(),
                                newJournal.getEditStatus(),
                                newJournal.getJournalId()
                        ));
                    }

                    populateLocalDbWithFetchedJournal(context, journalList);

                }else
                    queryJournalList(context);
            }
        });
    }

    private void populateLocalDbWithFetchedJournal(final Context context, final List<JournalEntry> journalList) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                //clean the local database
                mDb.journalDao().deleteAllJournals();

                //do a bulk insert into the local database
                for(JournalEntry journalEntry : journalList)
                    mDb.journalDao().insertJournal(journalEntry);

                //the service was started from the Splash Activity, hence start the Catalog activity
                AppExecutors.getInstance().onMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(isAppLaunch)
                            context.startActivity(new Intent(context, CatalogActivity.class));
                    }
                });
            }
        });
    }

    private void updateJournal(final Context context, final JournalEntry journal){
        DocumentReference journalRef = mFirestore.collection(COLLECTION_NAME).document(journal.getJournalId());

        Map<String, Object> update = new HashMap<>();
        update.put("journalTitle",journal.getJournalTitle());
        update.put("journalContent", journal.getJournalContent());
        update.put("editStatus", journal.getEditStatus());
        update.put("updatedAt", FieldValue.serverTimestamp());

        journalRef.update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful())
                    updateJournal(context, journal);
            }
        });
    }

    private void deleteJournal(final JournalEntry journal){
        DocumentReference journalRef = mFirestore.collection(COLLECTION_NAME).document(journal.getJournalId());

        journalRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful())
                    deleteJournal(journal);
            }
        });
    }
}
