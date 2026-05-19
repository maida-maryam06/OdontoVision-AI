package com.nmims.madproj.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nmims.madproj.R;

public class AR3DDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar3d_detail);
        com.nmims.madproj.utils.UiUtil.applyAmbientBackground(this);

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvConcept = findViewById(R.id.tvConcept);
        TextView tvAnatomy = findViewById(R.id.tvAnatomy);
        TextView tvClinical = findViewById(R.id.tvClinical);
        TextView tvFuture = findViewById(R.id.tvFuture);

        String topic = getIntent().getStringExtra("topic");

        if (topic == null) {
            topic = "Dentigerous Cyst";
        }

        if (topic.equals("Dentigerous Cyst")) {

            tvTitle.setText("Dentigerous Cyst 3D View");

            tvConcept.setText(
                    "3D/AR Concept:\n" +
                            "This module demonstrates a dentigerous cyst surrounding the crown of an unerupted or impacted tooth."
            );

            tvAnatomy.setText(
                    "Anatomical Relationship:\n" +
                            "The cyst is shown around the crown of an impacted tooth, usually near the cementoenamel junction. " +
                            "The model helps students understand how the lesion expands within the jaw and affects nearby teeth."
            );

            tvClinical.setText(
                    "Clinical and Radiographic Importance:\n" +
                            "Dentigerous cyst commonly presents as a well-defined unilocular radiolucency around the crown of an unerupted tooth. " +
                            "It is commonly associated with impacted mandibular third molars and maxillary canines."
            );

            tvFuture.setText(
                    "Future AR Implementation:\n" +
                            "A full AR version can allow students to rotate the impacted tooth and cyst in 3D, zoom into the lesion, and correlate it with radiographs."
            );

        } else if (topic.equals("Ameloblastoma")) {

            tvTitle.setText("Ameloblastoma Jaw Model");

            tvConcept.setText(
                    "3D/AR Concept:\n" +
                            "This module demonstrates a locally aggressive odontogenic tumor expanding within the posterior mandible."
            );

            tvAnatomy.setText(
                    "Anatomical Relationship:\n" +
                            "The 3D model shows expansion of the jaw bone, thinning of cortical plates, and relationship with molar-ramus region. " +
                            "This helps students understand why ameloblastoma can produce facial asymmetry and jaw swelling."
            );

            tvClinical.setText(
                    "Clinical and Radiographic Importance:\n" +
                            "Ameloblastoma is benign but locally aggressive. Radiographically, it may show multilocular radiolucency with soap-bubble or honeycomb appearance."
            );

            tvFuture.setText(
                    "Future AR Implementation:\n" +
                            "A full AR model can demonstrate tumor expansion, cortical perforation, tooth displacement, and surgical resection margins."
            );

        } else if (topic.equals("OKC")) {

            tvTitle.setText("Odontogenic Keratocyst 3D View");

            tvConcept.setText(
                    "3D/AR Concept:\n" +
                            "This module demonstrates an odontogenic keratocyst extending through the posterior mandible."
            );

            tvAnatomy.setText(
                    "Anatomical Relationship:\n" +
                            "OKC often grows in an anteroposterior direction within the medullary cavity. " +
                            "This 3D concept helps students understand why it can become large before obvious external swelling appears."
            );

            tvClinical.setText(
                    "Clinical and Radiographic Importance:\n" +
                            "OKC may appear as a unilocular or multilocular radiolucency. It is important because of its aggressive behavior and high recurrence tendency."
            );

            tvFuture.setText(
                    "Future AR Implementation:\n" +
                            "A full AR module can show cyst extension, satellite cysts, relation to mandibular canal, and areas requiring careful surgical curettage."
            );
        }
    }
}