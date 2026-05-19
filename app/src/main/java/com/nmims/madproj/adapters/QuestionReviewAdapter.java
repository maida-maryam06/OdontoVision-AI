/*
 * File: QuestionReviewAdapter.java
 * Purpose: RecyclerView adapter for result review with correctness indicators.
 */

package com.nmims.madproj.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nmims.madproj.models.Question;
import com.nmims.madproj.R;

import java.util.List;

public class QuestionReviewAdapter extends RecyclerView.Adapter<QuestionReviewAdapter.ReviewViewHolder> {

    private final List<Question> questions;
    private final List<Integer> userAnswers; // 1-4

    public QuestionReviewAdapter(List<Question> questions, List<Integer> userAnswers) {
        this.questions = questions;
        this.userAnswers = userAnswers;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_card, parent, false);
        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Question q = questions.get(position);
        holder.question.setText(q.getQuestionText());
        int ua = (position < userAnswers.size()) ? userAnswers.get(position) : 0;
        boolean correct = ua > 0 && ua == q.getCorrectAnswer();
        holder.icon.setImageResource(correct ? R.drawable.aurora_mac : R.drawable.aurora_mac);
        holder.explanation.setText(q.getExplanation() != null ? q.getExplanation() : "");
        holder.explanation.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(v -> {
            holder.explanation.setVisibility(holder.explanation.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public int getItemCount() {
        return questions != null ? questions.size() : 0;
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView question;
        TextView explanation;
        ImageView icon;
        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.questionText);
            explanation = itemView.findViewById(R.id.explanationText);
            icon = itemView.findViewById(R.id.statusIcon);
        }
    }
}


