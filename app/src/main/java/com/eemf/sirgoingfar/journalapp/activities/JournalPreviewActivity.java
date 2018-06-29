package com.eemf.sirgoingfar.journalapp.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.eemf.sirgoingfar.journalapp.R;
import com.eemf.sirgoingfar.journalapp.adapters.CatalogRecyclerViewAdapter;
import com.eemf.sirgoingfar.journalapp.database.AppDatabase;
import com.eemf.sirgoingfar.journalapp.database.JournalEntry;
import com.eemf.sirgoingfar.journalapp.models.AddJournalViewModel;
import com.eemf.sirgoingfar.journalapp.models.AddJournalViewModelFactory;
import com.eemf.sirgoingfar.journalapp.web_browser.DefaultWebBrowserFragment;
import com.eemf.sirgoingfar.journalapp.web_browser.WebViewHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.saket.bettermovementmethod.BetterLinkMovementMethod;

public class JournalPreviewActivity extends AppCompatActivity{

    private TextView mJournalTitle;
    private TextView mJournalContent;
    private TextView mLastUpdateLabel;
    private CardView mTitleCardView;
    private ScrollView mContentScrollView;
    private FrameLayout mFragmentContainer;

    public Fragment mCurrentFragment;
    public static final String EXTRA_INDEX = "journal_index";
    private int mJournalIndex;

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_preview);

        //reference UI views
        mJournalTitle = findViewById(R.id.tv_journal_title);
        mJournalContent = findViewById(R.id.tv_journal_content);
        mLastUpdateLabel = findViewById(R.id.tv_last_update_label);
        mTitleCardView = findViewById(R.id.cv_title);
        mContentScrollView = findViewById(R.id.sv_content);
        mFragmentContainer = findViewById(R.id.fragment_container);

        //get intent data
        if(getIntent().hasExtra(CatalogRecyclerViewAdapter.EXTRA_INDEX))
            mJournalIndex = getIntent().getIntExtra(CatalogRecyclerViewAdapter.EXTRA_INDEX, 0);

        mDb = AppDatabase.getInstance(this);

        fetchJournalFromDatabase(mJournalIndex);
    }

    private void fetchJournalFromDatabase(int journalIndex) {
        //Todo Fetch data from Db
        AddJournalViewModelFactory factory = new AddJournalViewModelFactory(mDb, journalIndex);
        final AddJournalViewModel model = ViewModelProviders.of(this, factory).get(AddJournalViewModel.class);
        model.getJournal().observe(this, new Observer<JournalEntry>() {
            @Override
            public void onChanged(@Nullable JournalEntry journalEntry) {
                populateUi(journalEntry);
            }
        });
    }

    private void populateUi(JournalEntry journal){
        //set the title bar title to the journal title
        ActionBar bar = getSupportActionBar();
        if(bar != null)
            bar.setTitle(journal.getJournalTitle());

        mLastUpdateLabel.setText(getString(R.string.last_update, journal.getEditStatus() == 1 ?
                getString(R.string.never_edited) :
                formatDate(journal.getUpdatedAt()).replace(".","")));

        //set the journal title and content
        mJournalTitle.setText(journal.getJournalTitle());
        mJournalContent.setText(journal.getJournalContent());
        BetterLinkMovementMethod.linkify(Linkify.WEB_URLS, mJournalContent)
                .setOnLinkClickListener(new BetterLinkMovementMethod.OnLinkClickListener() {
                    @Override
                    public boolean onClick(TextView textView, String url) {

                        startWebBrowser(url);

                        return true;
                    }
                })
                .setOnLinkLongClickListener(new BetterLinkMovementMethod.OnLinkLongClickListener() {
                    @Override
                    public boolean onLongClick(TextView textView, String url) {
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData data = ClipData.newPlainText(getString(R.string.copied_link_clipboard_label), url);
                        clipboardManager.setPrimaryClip(data);

                        Toast.makeText(JournalPreviewActivity.this,getString(R.string.copied_link_toast_msg_content),Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_journal_preview_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit_journal:
                Intent editJournal = new Intent(this, AddJournalActivity.class);
                editJournal.putExtra(EXTRA_INDEX, mJournalIndex);
                startActivity(editJournal);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment frag = getmCurrentFragment();
        if(frag instanceof DefaultWebBrowserFragment) {
            DefaultWebBrowserFragment browserFragment = (DefaultWebBrowserFragment) frag;
            browserFragment.onBackPress();
        }else
            super.onBackPressed();
    }

    public Fragment getmCurrentFragment() {
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (count > 0) {
            FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(count - 1);
            String tag = entry.getName();
            return fm.findFragmentByTag(tag);
        }
        return null;
    }

    public void startFragment(Fragment fragment, boolean addToBackStack) {
        setContainerVisibility(true);

        if (fragment != null) {
            mCurrentFragment = fragment;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment, fragment.getClass().getName());
            if (addToBackStack) {
                ft.addToBackStack(fragment.getClass().getName());
            }
            ft.commit();
        }
    }

    public void closeFragment() {
        setContainerVisibility(false);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    public void startWebBrowser(String url) {
        WebViewHandler handler = new WebViewHandler(JournalPreviewActivity.this, url);
        handler.processUrl();
    }

    private void setContainerVisibility(boolean isVisible){
        //toggle action bar
        ActionBar bar = getSupportActionBar();
        if(bar != null){
            if(isVisible)
                bar.hide();
            else
                bar.show();
        }

        if(isVisible){
            mContentScrollView.setVisibility(View.GONE);
            mTitleCardView.setVisibility(View.GONE);
            mFragmentContainer.setVisibility(View.VISIBLE);
        }else {
            mContentScrollView.setVisibility(View.VISIBLE);
            mTitleCardView.setVisibility(View.VISIBLE);
            mFragmentContainer.setVisibility(View.GONE);
        }
    }

    private String formatDate(Date date){
        String DATE_FORMAT = "EEE, d MMMM yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return dateFormat.format(date);
    }
}

