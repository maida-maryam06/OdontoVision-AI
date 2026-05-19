/*
 * File: QuizHistoryAdapter.java
 * Purpose: RecyclerView adapter to display past quiz results in stats screen.
 */

package com.nmims.madproj.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nmims.madproj.models.Quiz;
import com.nmims.madproj.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizHistoryAdapter extends RecyclerView.Adapter<QuizHistoryAdapter.HistoryViewHolder> {

    public interface OnQuizClickListener {
        void onQuizClick(Quiz quiz);
    }

    private final List<Quiz> quizzes;
    private final OnQuizClickListener listener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());

    public QuizHistoryAdapter(List<Quiz> quizzes, OnQuizClickListener listener) {
        this.quizzes = quizzes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_card, parent, false);
        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Quiz q = quizzes.get(position);
        holder.title.setText(q.getTitle());
        holder.subtitle.setText(q.getCategory() + " • " + q.getScore() + "/" + q.getTotalQuestions());
        holder.time.setText(sdf.format(new Date(q.getTimestamp())));
        holder.itemView.setOnClickListener(v -> { if (listener != null) listener.onQuizClick(q); });
    }

    @Override
    public int getItemCount() { return quizzes != null ? quizzes.size() : 0; }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, time;
        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            time = itemView.findViewById(R.id.time);
        }
    }
}


