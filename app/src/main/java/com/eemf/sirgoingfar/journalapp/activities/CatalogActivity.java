package com.eemf.sirgoingfar.journalapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.eemf.sirgoingfar.journalapp.R;
import com.eemf.sirgoingfar.journalapp.adapters.CatalogRecyclerViewAdapter;
import com.eemf.sirgoingfar.journalapp.database.DateConverter;
import com.eemf.sirgoingfar.journalapp.models.JournalEntity;

import java.util.ArrayList;

public class CatalogActivity extends AppCompatActivity {

    private CatalogRecyclerViewAdapter adapter;
    private ArrayList<JournalEntity> journal = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        journal.add(new JournalEntity(
                1,
                "Data Analysis",
                "Data Analysis is the breaking down of data into its simplest mode.",
                DateConverter.toDate(123346L),
                DateConverter.toDate(23456L),
                1
        ));

        journal.add(new JournalEntity(
                2,
                "Tales of Two Lovers",
                "When we started our love!",
                DateConverter.toDate(1346L),
                DateConverter.toDate(256L),
                2
        ));

        adapter = new CatalogRecyclerViewAdapter(this, journal);

        RecyclerView catalogRecyclerView = findViewById(R.id.rv_journal_catalog);
        catalogRecyclerView.setHasFixedSize(true);
        catalogRecyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //Todo perform the swipe to delete operation
            }
        }).attachToRecyclerView(catalogRecyclerView);

        FloatingActionButton fab = findViewById(R.id.fab_add_new_journal);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewJournal();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                logoutUser();
                return true;

            case R.id.action_insert_new_journal:
                addNewJournal();
                return true;

            case R.id.action_delete_all_journals:
                deleteAllJournals();
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
        //Todo: Delete all journals in the DB and Firestore
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
}

