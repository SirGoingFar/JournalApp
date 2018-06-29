package com.eemf.sirgoingfar.journalapp.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.eemf.sirgoingfar.journalapp.R;
import com.eemf.sirgoingfar.journalapp.adapters.CatalogRecyclerViewAdapter;
import com.eemf.sirgoingfar.journalapp.database.AppDatabase;
import com.eemf.sirgoingfar.journalapp.database.AppExecutors;
import com.eemf.sirgoingfar.journalapp.database.JournalEntry;
import com.eemf.sirgoingfar.journalapp.models.CatalogViewModel;

import java.util.List;

public class CatalogActivity extends AppCompatActivity {

    private CatalogRecyclerViewAdapter mAdapter;
    private AppDatabase mDb;
    private AppExecutors mExecutor;

    private FrameLayout mEmptyStateView;
    private RecyclerView mCatalogRecyclerView;
    private List<JournalEntry> currentJournalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        //initialize the connectors
        mDb = AppDatabase.getInstance(this);
        mExecutor = AppExecutors.getInstance();

        //initialize UI components
        mEmptyStateView = findViewById(R.id.fl_empty_state_view);
        mAdapter = new CatalogRecyclerViewAdapter(this);

        mCatalogRecyclerView = findViewById(R.id.rv_journal_catalog);
        mCatalogRecyclerView.setHasFixedSize(true);
        mCatalogRecyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<JournalEntry> journals = mAdapter.getJournals();
                        mDb.journalDao().deleteJournal(journals.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mCatalogRecyclerView);

        FloatingActionButton fab = findViewById(R.id.fab_add_new_journal);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewJournal();
            }
        });

        setupViewModel();
    }

    private void setupViewModel() {
        CatalogViewModel model = ViewModelProviders.of(this).get(CatalogViewModel.class);
        model.getJournals().observe(this, new Observer<List<JournalEntry>>() {
            @Override
            public void onChanged(@Nullable List<JournalEntry> journalEntries) {
                mAdapter.setJournal(journalEntries);
                currentJournalList = journalEntries;
                showCorrectView(journalEntries.size() < 1);
            }
        });
    }

    private void showCorrectView(boolean isCatalogEmpty) {
        if(isCatalogEmpty) {
            mEmptyStateView.setVisibility(View.VISIBLE);
            mCatalogRecyclerView.setVisibility(View.GONE);
        }
        else {
            mEmptyStateView.setVisibility(View.GONE);
            mCatalogRecyclerView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout_list:
            case R.id.action_logout:
                logoutUser();
                return true;

            case R.id.action_insert_new_journal:
                addNewJournal();
                return true;

            case R.id.action_delete_all_journals:
                if(currentJournalList.size() > 0) {
                    createAlertDialog(getString(R.string.delete_all_journals_prompt))
                            .setNegativeButton(getString(R.string.alert_dialog_negative_button_label), null)
                            .setPositiveButton(getString(R.string.alert_dialog_positive_button_label), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteAllJournals();
                                }
                            }).create().show();
                }
                return true;

            case R.id.action_setting:
                //Todo: Launch the SettingssPreferenceFragment

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    private void addNewJournal() {
        startActivity(new Intent(this, AddJournalActivity.class));
    }

    private void deleteAllJournals() {
        mExecutor.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.journalDao().deleteAllJournals();
            }
        });
    }

    private void logoutUser() {
        //Todo: Log user out completely and clear all necessary preferences
    }


    /*
     * on @value KEYCODE_BACK is pressed, @link onKeyDown(keyCode, event) is called
     * @param keyCode is the key code for the key pressed
     * @param event is the key event
     * @return true or @return the event*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                Intent minimize = new Intent(Intent.ACTION_MAIN);
                minimize.addCategory(Intent.CATEGORY_HOME);
                minimize.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(minimize);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private AlertDialog.Builder createAlertDialog(String message){
        return new AlertDialog.Builder(this)
                .setMessage(message);
    }
}

