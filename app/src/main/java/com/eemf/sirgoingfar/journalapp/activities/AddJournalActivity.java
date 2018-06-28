package com.eemf.sirgoingfar.journalapp.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.eemf.sirgoingfar.journalapp.R;
import com.eemf.sirgoingfar.journalapp.adapters.CatalogRecyclerViewAdapter;


public class AddJournalActivity extends AppCompatActivity{
    private EditText mJournalTitle;
    private EditText mJournalContent;
    private boolean isEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        //instantiate UI references
        mJournalTitle = findViewById(R.id.et_journal_title);
        mJournalContent = findViewById(R.id.et_journal_content);

        int journalIndex = 0;

        if(getIntent().hasExtra(CatalogRecyclerViewAdapter.EXTRA_INDEX)){
            journalIndex = getIntent().getIntExtra(CatalogRecyclerViewAdapter.EXTRA_INDEX, -1);
            isEditing = journalIndex > -1;
        }

        if(isEditing)
            setupView(journalIndex);
    }

    private void setupView(int journalIndex) {
        //Todo: load journal from DB using the journalDbIndex and set the view
        mJournalTitle.setText("Journal Title at position " + journalIndex);
        mJournalContent.setText("Journal Content at position " + journalIndex);

        //set the field selection
        mJournalTitle.setSelection(mJournalTitle.getText().toString().length());

        //set the content field's selection whenever it receives focus
        mJournalContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    mJournalContent.setSelection(mJournalContent.getText().toString().length());
            }
        });

        //change ActionBar label
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null)
        actionBar.setTitle(R.string.actionbar_title_edit_journal);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_journal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save_new_journal:
                //Todo: Check if it's for adding new journal or edit existing journal
                if(isEditing){
                    createAlertDialog("Do you want to save changes to this Journal?")
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //close the edit activity
                                    finish();
                                }
                            })
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Todo: save to local DB and update Firestore
                                }
                            }).create().show();
                } else{
                    //Todo: Save to local Db and Firestore
                }
                return true;
            case R.id.action_cancel_editing:
                //Todo: Check if it's for adding new journal or edit existing journal
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AlertDialog.Builder createAlertDialog(String message){
        return new AlertDialog.Builder(this)
                .setMessage(message);
    }
}

