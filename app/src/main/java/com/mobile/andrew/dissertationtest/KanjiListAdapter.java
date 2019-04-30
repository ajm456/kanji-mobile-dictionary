package com.mobile.andrew.dissertationtest;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mobile.andrew.dissertationtest.asyncTasks.UpdateFavouritedParams;
import com.mobile.andrew.dissertationtest.asyncTasks.UpdateFavouritedTask;
import com.mobile.andrew.dissertationtest.room.KanjiData;
import com.wefika.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

public class KanjiListAdapter extends RecyclerView.Adapter<KanjiListAdapter.KanjiViewHolder>
{
    private final String TAG = KanjiListAdapter.class.getSimpleName();

    private final Activity parentActivity;

    private final List<KanjiData> kanjiData;
    private KanjiData recentlyDeletedItem;
    private int recentlyDeletedItemPosition;
    private static float[] searchScores;

    class KanjiViewHolder extends RecyclerView.ViewHolder
    {
        private final View itemView;
        private final TextView tvKanji;
        private final TextView tvMeanings;
        private final FlowLayout flReadingsHolder;

        KanjiViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvKanji = itemView.findViewById(R.id.text_resultitem_kanji);
            tvMeanings = itemView.findViewById(R.id.text_resultitem_meanings);
            flReadingsHolder = itemView.findViewById(R.id.flowlayout_resultitem_readings);
        }

        void bind(final KanjiData kanjiData) {
            // Fill the display fields
            tvKanji.setText(kanjiData.character);
            tvMeanings.setText(TextUtils.join(", ", kanjiData.getMeaningsArr()));
            for(String kunReading : kanjiData.getKunReadingsArr()) {
                TextView reading = new TextView(App.getContext());
                reading.setText(kunReading);
                FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4,4,4,4);
                reading.setLayoutParams(params);
                reading.setBackground(App.getContext().getDrawable(R.drawable.rounded_corners_kun));
                reading.setTextColor(ContextCompat.getColor(App.getContext(), R.color.white));
                flReadingsHolder.addView(reading);
            }
            for(String onReading : kanjiData.getOnReadingsArr()) {
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
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), KanjiDetailsActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                // Pass the kanji data
                intent.putExtra("data", kanjiData);
                // Pass the search scores so the activity can update the dictionary
                intent.putExtra("complex_score", searchScores[0]);
                intent.putExtra("symm_score", searchScores[1]);
                intent.putExtra("diag_score", searchScores[2]);
                itemView.getContext().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(parentActivity).toBundle());
            });
        }
    }

    KanjiListAdapter(Activity parentActivity) {
        this.parentActivity = parentActivity;
        kanjiData = new ArrayList<>();
    }

    public void updateData(List<KanjiData> newKanjiData, float[] newSearchScores) {
        kanjiData.clear();
        kanjiData.addAll(newKanjiData);
        notifyDataSetChanged();
        searchScores = newSearchScores;
    }

    public void updateData(List<KanjiData> newKanjiData) {
        kanjiData.clear();
        kanjiData.addAll(newKanjiData);
        notifyDataSetChanged();
        searchScores = new float[] { -1f, -1f, -1f };
    }

    KanjiData getItem(int position) {
        return kanjiData.get(position);
    }

    void deleteItem(int position) {
        UpdateFavouritedParams params = new UpdateFavouritedParams(kanjiData.get(position).character, 0);
        new UpdateFavouritedTask().execute(params);
        recentlyDeletedItem = kanjiData.get(position);
        recentlyDeletedItemPosition = position;
        kanjiData.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
        FavouritesActivity favouritesActivity = (FavouritesActivity) parentActivity;
        favouritesActivity.checkHint();
    }

    private void showUndoSnackbar() {
        View view = parentActivity.findViewById(R.id.root_favourites);
        Snackbar snackbar = Snackbar.make(view, R.string.all_snackbar_deletedtext, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.all_snackbar_undotext, v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
        UpdateFavouritedParams params = new UpdateFavouritedParams(recentlyDeletedItem.character, 1);
        new UpdateFavouritedTask().execute(params);
        kanjiData.add(recentlyDeletedItemPosition, recentlyDeletedItem);
        notifyItemInserted(recentlyDeletedItemPosition);
        FavouritesActivity favouritesActivity = (FavouritesActivity) parentActivity;
        favouritesActivity.checkHint();
    }

    @NonNull
    @Override
    public KanjiListAdapter.KanjiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new KanjiViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_searchresult, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull KanjiViewHolder holder, int position) {
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