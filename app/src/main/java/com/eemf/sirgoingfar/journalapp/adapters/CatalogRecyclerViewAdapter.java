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
import com.eemf.sirgoingfar.journalapp.activities.AddJournalActivity;
import com.eemf.sirgoingfar.journalapp.models.JournalEntity;

import java.util.ArrayList;

public class CatalogRecyclerViewAdapter extends RecyclerView.Adapter<CatalogRecyclerViewAdapter.Holder>{

    private ArrayList<JournalEntity> journal;
    private Context context;
    public static final String EXTRA_INDEX = "journal_index";

    public CatalogRecyclerViewAdapter(Context context, ArrayList<JournalEntity> journal) {
        this.context = context;
        this.journal = journal;
    }

    private JournalEntity getJournal(int position) {
        return journal.get(position);
    }

    public void setJournal(ArrayList<JournalEntity> journal) {
        this.journal = journal;
        notifyDataSetChanged();
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

        //Todo date conversion here
        holder.journalCreatedAt.setText(getJournal(position).getCreatedAt().toString());
        holder.journalEditStatus.setText(getJournal(position).getEditStatus() == 1
                ? context.getString(R.string.keyword_unedited) : context.getString(R.string.keyword_edited));

        //process the edit status color code
        holder.journalEditStatus.setChecked(getJournal(position).getEditStatus() != 1);

        //set tag for easy reference
        holder.container.setTag(getJournal(position).get_id());

        //animate holders
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left));
    }

    @Override
    public int getItemCount() {
        return journal.size();
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
                        Intent editJournal = new Intent(context, AddJournalActivity.class);
                        editJournal.putExtra(EXTRA_INDEX, Integer.valueOf(itemView.getTag().toString()));
                        context.startActivity(editJournal);
                    }
                });
        }
    }
}
