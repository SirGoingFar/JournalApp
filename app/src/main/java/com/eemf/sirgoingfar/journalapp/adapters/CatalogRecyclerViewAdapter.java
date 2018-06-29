package com.eemf.sirgoingfar.journalapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.eemf.sirgoingfar.journalapp.R;
import com.eemf.sirgoingfar.journalapp.activities.JournalPreviewActivity;
import com.eemf.sirgoingfar.journalapp.database.JournalEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CatalogRecyclerViewAdapter extends RecyclerView.Adapter<CatalogRecyclerViewAdapter.Holder>{

    private List<JournalEntry> journal;
    private Context context;
    public static final String EXTRA_INDEX = "journal_index";

    public CatalogRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    private JournalEntry getJournal(int position) {
        return journal.get(position);
    }

    public void setJournal(List<JournalEntry> journals) {
        this.journal = journals;
        notifyDataSetChanged();
    }

    private String formatDate(Date date){
        String DATE_FORMAT = "EEE, d MMMM yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return dateFormat.format(date);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_journal,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.journalTitle.setText(getJournal(position).getJournalTitle());
        holder.journalPreview.setText(getJournal(position).getJournalContent());
        holder.journalCreatedAt.setText(formatDate(getJournal(position).getCreatedAt()));
        holder.journalEditStatus.setText(getJournal(position).getEditStatus() == 1
                ? context.getString(R.string.keyword_unedited) : context.getString(R.string.keyword_edited));

        //process the edit status color code
        holder.journalEditStatus.setChecked(getJournal(position).getEditStatus() != 1);

        //set tag for easy reference
        holder.container.setTag(getJournal(position).getId());

        //animate holders
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left));
    }

    @Override
    public int getItemCount() {
        if(journal != null)
            return journal.size();
        else
            return 0;
    }

    public List<JournalEntry> getJournals() {
        return journal;
    }

    class Holder extends RecyclerView.ViewHolder{

        TextView journalTitle;
        TextView journalPreview;
        TextView journalCreatedAt;
        CheckedTextView journalEditStatus;
        CardView container;

        Holder(final View itemView) {
            super(itemView);

            journalTitle = itemView.findViewById(R.id.tv_journal_title);
            journalPreview = itemView.findViewById(R.id.tv_journal_preview);
            journalCreatedAt = itemView.findViewById(R.id.tv_created_at);
            journalEditStatus = itemView.findViewById(R.id.ct_edit_status);
            container = itemView.findViewById(R.id.list_item_container);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent previewJournal = new Intent (context, JournalPreviewActivity.class);
                    previewJournal.putExtra(EXTRA_INDEX, Integer.valueOf(itemView.getTag().toString()));
                    context.startActivity(previewJournal);
                }
            });
        }
    }
}
