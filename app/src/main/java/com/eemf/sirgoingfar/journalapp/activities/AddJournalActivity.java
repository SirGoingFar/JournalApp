package com.eemf.sirgoingfar.journalapp.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eemf.sirgoingfar.journalapp.R;
import com.eemf.sirgoingfar.journalapp.database.AppDatabase;
import com.eemf.sirgoingfar.journalapp.database.AppExecutors;
import com.eemf.sirgoingfar.journalapp.database.JournalEntry;
import com.eemf.sirgoingfar.journalapp.models.AddJournalViewModel;
import com.eemf.sirgoingfar.journalapp.models.AddJournalViewModelFactory;

import java.util.Date;


public class AddJournalActivity extends AppCompatActivity{
    private EditText mJournalTitle;
    private EditText mJournalContent;
    private boolean isEditing;

    private AppDatabase mDb;
    private AppExecutors mExecutors;
    private JournalEntry mJournalEntry;

    private int journalIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        //instantiate UI references
        mJournalTitle = findViewById(R.id.et_journal_title);
        mJournalContent = findViewById(R.id.et_journal_content);

        //initialize the AppDatabase instance
        mDb = AppDatabase.getInstance(getApplicationContext());

        //initialize the AppExecutor instance
        mExecutors = AppExecutors.getInstance();

        if(getIntent().hasExtra(JournalPreviewActivity.EXTRA_INDEX)){
            journalIndex = getIntent().getIntExtra(JournalPreviewActivity.EXTRA_INDEX, -1);
            isEditing = journalIndex > -1;
        }

        if(isEditing){
            AddJournalViewModelFactory factory = new AddJournalViewModelFactory(mDb, journalIndex);
            final AddJournalViewModel model = ViewModelProviders.of(this, factory).get(AddJournalViewModel.class);
            model.getJournal().observe(this, new Observer<JournalEntry>() {
                @Override
                public void onChanged(@Nullable JournalEntry journalEntry) {
                    model.getJournal().removeObserver(this);
                    setupView(journalEntry);
                    mJournalEntry = journalEntry;
                }
            });
        }
    }

    private void setupView(JournalEntry journal) {

        mJournalTitle.setText(journal.getJournalTitle());
        mJournalContent.setText(journal.getJournalContent());

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

        switch (item.getItemId()){
            case R.id.action_save_new_journal:
                if(isEditing){
                    if(!title.equalsIgnoreCase(mJournalEntry.getJournalTitle()) ||
                            !content.equalsIgnoreCase(mJournalEntry.getJournalContent())){
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
                                        validateInputFields(content, title, mJournalEntry.getCreatedAt());
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
                                        validateInputFields(content, title, null);
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
                    if(!mJournalEntry.getJournalContent().equalsIgnoreCase(content)
                            || !mJournalEntry.getJournalTitle().equalsIgnoreCase(title)) {
                        createAlertDialog(getString(R.string.ignore_journal_edit_notif_msg))
                                .setNegativeButton(R.string.alert_dialog_negative_button_label, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //validate and then save to DB
                                        validateInputFields(content, title, mJournalEntry.getCreatedAt());
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
                                        validateInputFields(content, title, null);
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

    private void validateInputFields(String content, String title, Date createdAt) {
        if(content.isEmpty()) {
            mJournalContent.setError(getString(R.string.content_not_empty_error_msg));
        }

        if(title.isEmpty()) {
            mJournalTitle.setError(getString(R.string.title_not_empty_error_msg));
        }

        if(!content.isEmpty() && !title.isEmpty()) {
            saveToDatabase(title, content, createdAt);
            saveToFirestore(title, content, createdAt);
            finish();
        }
    }

    private void saveToFirestore(String title, String content, Date createdAt) {
        //Todo: Save to Firestore
    }

    private void saveToDatabase(String title, String content, Date createdAt) {
        int editStatus = isEditing ? 2 : 1;

        if(createdAt == null)
            createdAt = new Date();

        final JournalEntry journal = new JournalEntry(title, content, createdAt, new Date(), editStatus);

        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(isEditing) {
                    journal.setId(journalIndex);
                    mDb.journalDao().updateJournal(journal);
                }
                else
                    mDb.journalDao().insertJournal(journal);
            }
        });
    }

    private AlertDialog.Builder createAlertDialog(String message){
        return new AlertDialog.Builder(this)
                .setMessage(message);
    }
}

