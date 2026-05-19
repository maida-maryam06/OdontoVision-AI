package com.nmims.madproj.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.nmims.madproj.R;

public class ARLearningActivity extends AppCompatActivity {

    private MaterialButton btnDentigerous;
    private MaterialButton btnAmeloblastoma;
    private MaterialButton btnOKC;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_learning);

        com.nmims.madproj.utils.UiUtil.applyAmbientBackground(this);

        btnDentigerous = findViewById(R.id.btnDentigerous3D);
        btnAmeloblastoma = findViewById(R.id.btnAmeloblastoma3D);
        btnOKC = findViewById(R.id.btnOKC3D);

        btnDentigerous.setOnClickListener(v -> {
            Intent intent = new Intent(ARLearningActivity.this, ModelViewerActivity.class);
            intent.putExtra("topic", "Dentigerous Cyst 3D View");
            intent.putExtra("modelUrl", "models/maxillary_canine.glb");
            startActivity(intent);
        });

        btnAmeloblastoma.setOnClickListener(v -> {
            Intent intent = new Intent(ARLearningActivity.this, ModelViewerActivity.class);
            intent.putExtra("topic", "Ameloblastoma Jaw Model");
            intent.putExtra("modelUrl", "models/mandible.glb");
            startActivity(intent);
        });

        btnOKC.setOnClickListener(v -> {
            Intent intent = new Intent(ARLearningActivity.this, ModelViewerActivity.class);
            intent.putExtra("topic", "Odontogenic Keratocyst Mandibular Lesion");
            intent.putExtra("modelUrl", "models/mandible.glb");
            startActivity(intent);
        });
    }
}