/*
 * File: QuizSelectionActivity.java
 * Purpose: Lets users choose category and difficulty; launches quiz.
 */

package com.nmims.madproj.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.nmims.madproj.R;
import com.nmims.madproj.adapters.CategoryAdapter;
import com.nmims.madproj.repository.QuizRepository;
import com.nmims.madproj.utils.Constants;

import java.util.List;
import java.util.concurrent.Executors;

public class QuizSelectionActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {

    private CategoryAdapter adapter;
    private QuizRepository repository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_selection);
        com.nmims.madproj.utils.UiUtil.applyAmbientBackground(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Select Category");
        }

        repository = new QuizRepository(getApplication());

        RecyclerView rv = findViewById(R.id.recyclerCategories);
        rv.setLayoutManager(new LinearLayoutManager(this));
        
        // Load categories from database on background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            List<String> categories = repository.getDistinctCategories();
            runOnUiThread(() -> {
                adapter = new CategoryAdapter(this, categories);
                rv.setAdapter(adapter);
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh categories when returning to this screen (in case new quizzes were added)
        Executors.newSingleThreadExecutor().execute(() -> {
            List<String> updatedCategories = repository.getDistinctCategories();
            runOnUiThread(() -> adapter.updateCategories(updatedCategories));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onCategoryClick(String category) {
        Snackbar.make(findViewById(android.R.id.content), "Selected: " + category, Snackbar.LENGTH_SHORT).show();
        Intent i = new Intent(this, QuizActivity.class);
        i.putExtra("category", category);
        i.putExtra("difficulty", Constants.DIFFICULTY_MEDIUM);
        startActivity(i);
    }
}


