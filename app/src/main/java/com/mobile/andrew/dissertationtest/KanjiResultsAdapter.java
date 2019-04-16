package com.mobile.andrew.dissertationtest;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobile.andrew.dissertationtest.db.KanjiData;
import com.wefika.flowlayout.FlowLayout;

import java.util.ArrayList;

public class KanjiResultsAdapter extends RecyclerView.Adapter<KanjiResultsAdapter.KanjiViewHolder>
{
    private final String TAG = KanjiResultsAdapter.class.getSimpleName();

    private Activity parentActivity;

    private ArrayList<KanjiData> kanjiData;
    private static Float[] searchScores;

    class KanjiViewHolder extends RecyclerView.ViewHolder
    {
        private View itemView;
        private TextView tvKanji, tvMeanings;
        private FlowLayout flReadingsHolder;

        KanjiViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvKanji = itemView.findViewById(R.id.text_resultitem_kanji);
            tvMeanings = itemView.findViewById(R.id.text_resultitem_meanings);
            flReadingsHolder = itemView.findViewById(R.id.flowlayout_resultitem_readings);
        }

        void bind(final KanjiData kanjiData) {
            // Fill the display fields
            tvKanji.setText(String.valueOf(kanjiData.kanji));
            tvMeanings.setText(TextUtils.join(", ", kanjiData.meanings));
            for(String kunReading : kanjiData.kunReadings) {
                TextView reading = new TextView(App.getContext());
                reading.setText(kunReading);
                FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4,4,4,4);
                reading.setLayoutParams(params);
                reading.setBackground(App.getContext().getDrawable(R.drawable.rounded_corners_kun));
                reading.setTextColor(ContextCompat.getColor(App.getContext(), R.color.white));
                flReadingsHolder.addView(reading);
            }
            for(String onReading : kanjiData.onReadings) {
                TextView reading = new TextView(App.getContext());
                reading.setText(onReading);
                FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4,4,4,4);
                reading.setLayoutParams(params);
                reading.setBackground(App.getContext().getDrawable(R.drawable.rounded_corners_on));
                reading.setTextColor(ContextCompat.getColor(App.getContext(), R.color.white));
                flReadingsHolder.addView(reading);
            }

            // Set an OnClickListener so that the KanjiDetailsActivity is launched with the
            // displayed kanji character
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), KanjiDetailsActivity.class);
                    intent.setAction(Intent.ACTION_VIEW);
                    // Pass the kanji data
                    intent.putExtra("data", kanjiData);
                    // Pass the search scores so the activity can update the dictionary
                    intent.putExtra("complex_score", searchScores[0]);
                    intent.putExtra("symm_score", searchScores[1]);
                    intent.putExtra("diag_score", searchScores[2]);
                    itemView.getContext().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(parentActivity).toBundle());
                }
            });
        }
    }

    KanjiResultsAdapter(ArrayList<KanjiData> kanjiData, Float[] searchScores, Activity activity) {
        this.kanjiData = kanjiData;
        KanjiResultsAdapter.searchScores = searchScores;
        this.parentActivity = activity;
    }

    void updateData(ArrayList<KanjiData> newKanjiData, Float[] newSearchScores) {
        Log.d(TAG, "Started updating data!");
        kanjiData.clear();
        kanjiData.addAll(newKanjiData);
        notifyDataSetChanged();
        searchScores = newSearchScores;
        Log.d(TAG, "Finished updating data!");
    }

    @Override
    public KanjiResultsAdapter.KanjiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new KanjiViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_searchresult, parent, false));
    }

    @Override
    public void onBindViewHolder(KanjiViewHolder holder, int position) {
        holder.bind(kanjiData.get(position));
    }

    @Override
    public int getItemCount() {
        return kanjiData.size();
    }

    @Override
    public void onViewRecycled(@NonNull KanjiViewHolder holder) {
        super.onViewRecycled(holder);
        holder.flReadingsHolder.removeAllViews();
    }
}