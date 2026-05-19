/*
 * File: CategoryAdapter.java
 * Purpose: RecyclerView adapter to display quiz categories.
 */

package com.nmims.madproj.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.nmims.madproj.utils.Constants;
import com.nmims.madproj.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    private final List<String> categories = new ArrayList<>();
    private final OnCategoryClickListener listener;

    public CategoryAdapter(OnCategoryClickListener listener, List<String> categoryList) {
        this.listener = listener;
        if (categoryList != null && !categoryList.isEmpty()) {
            categories.addAll(categoryList);
        } else {
            // Fallback to default categories
            categories.add(Constants.CATEGORY_JAVA);
            categories.add(Constants.CATEGORY_ANDROID);
            categories.add(Constants.CATEGORY_DSA);
        }
    }

    public void updateCategories(List<String> newCategories) {
        categories.clear();
        if (newCategories != null && !newCategories.isEmpty()) {
            categories.addAll(newCategories);
        } else {
            categories.add(Constants.CATEGORY_JAVA);
            categories.add(Constants.CATEGORY_ANDROID);
            categories.add(Constants.CATEGORY_DSA);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_card, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String cat = categories.get(position);
        holder.title.setText(cat);
        holder.card.setOnClickListener(v -> {
            if (listener != null) listener.onCategoryClick(cat);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView title;
        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            title = itemView.findViewById(R.id.title);
        }
    }
}


