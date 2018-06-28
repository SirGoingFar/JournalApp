package com.eemf.sirgoingfar.journalapp.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eemf.sirgoingfar.journalapp.R;
import com.eemf.sirgoingfar.journalapp.adapters.CatalogRecyclerViewAdapter;
import com.eemf.sirgoingfar.journalapp.database.DateConverter;
import com.eemf.sirgoingfar.journalapp.models.JournalEntity;


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
        final String title = mJournalTitle.getText().toString().trim();
        final String content = mJournalContent.getText().toString().trim();
        JournalEntity valueFetchedFromDB = new JournalEntity(
                1,
                "Journal Title at position 1",
                "Journal Content at position 1",
                DateConverter.toDate(123346L),
                DateConverter.toDate(23456L),
                1
        );
        switch (item.getItemId()){
            case R.id.action_save_new_journal:
                if(isEditing){
                    if(!title.equalsIgnoreCase(valueFetchedFromDB.getJournalTitle()) ||
                            !content.equalsIgnoreCase(valueFetchedFromDB.getJournalContent())){
                        createAlertDialog(getString(R.string.journal_edit_notif_msg))
                                .setNegativeButton(R.string.alert_dialog_negative_button_label, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //close the edit activity
                                        finish();
                                    }
                                })
                                .setPositiveButton(R.string.alert_dialog_positive_button_label, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //validate and then save to DB
                                        validateInputFields(content, title);
                                    }
                                }).create().show();
                    }else {
                        //no editing made
                        finish();
                    }
                } else{
                   if(!content.isEmpty() || !title.isEmpty()){
                       createAlertDialog(getString(R.string.new_journal_save_notif_msg))
                               .setNegativeButton(R.string.alert_dialog_negative_button_label, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       //close the edit activity
                                       finish();
                                   }
                               })
                               .setPositiveButton(R.string.alert_dialog_positive_button_label, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       //validate and then save to DB
                                       validateInputFields(content, title);
                                   }
                               }).create().show();
                   }else{
                       //nothing entered, just close the activity
                       finish();
                       Toast.makeText(this, R.string.nothing_to_save_toast_msg, Toast.LENGTH_SHORT).show();
                   }
                }
                return true;
            case R.id.action_cancel_editing:
                if(isEditing){
                    //Todo: check if there's an actual edit - "valueFetchedFromDB" is from the DB
                    if(!valueFetchedFromDB.getJournalContent().equalsIgnoreCase(content)
                            || !valueFetchedFromDB.getJournalTitle().equalsIgnoreCase(title)) {
                        createAlertDialog(getString(R.string.ignore_journal_edit_notif_msg))
                                .setNegativeButton(R.string.alert_dialog_negative_button_label, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //validate and then save to DB
                                        validateInputFields(content, title);
                                    }
                                })
                                .setPositiveButton(R.string.alert_dialog_positive_button_label, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //close the edit activity
                                        finish();
                                    }
                                }).create().show();
                    }else{
                        //no edit made, just close the editing activity
                        finish();
                    }
                } else{
                    if(!content.isEmpty() || !title.isEmpty()) {
                        createAlertDialog(getString(R.string.close_journal_without_saving_notif_msg))
                                .setNegativeButton(R.string.alert_dialog_negative_button_label, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //validate and then save to DB
                                        validateInputFields(content, title);
                                    }
                                })
                                .setPositiveButton(R.string.alert_dialog_positive_button_label, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //close the edit activity
                                        finish();
                                    }
                                }).create().show();
                    }else {
                        //nothing was entered, just close the activity
                        finish();
                        Toast.makeText(this, R.string.nothing_to_save_toast_msg, Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void validateInputFields(String content, String title) {
        if(content.isEmpty()) {
            mJournalContent.setError(getString(R.string.content_not_empty_error_msg));
        }

        if(title.isEmpty()) {
            mJournalTitle.setError(getString(R.string.title_not_empty_error_msg));
        }

        if(!content.isEmpty() && !title.isEmpty()) {
            // Todo: Else save to local DB and save on Firestore too
            // close the activity too
            Log.d(AddJournalActivity.class.getSimpleName(), "Data added to DB");
        }
    }

    private AlertDialog.Builder createAlertDialog(String message){
        return new AlertDialog.Builder(this)
                .setMessage(message);
    }
}

