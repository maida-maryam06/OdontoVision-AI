/*
 * File: UploadedNote.java
 * Purpose: Room entity for OCR result storage and metadata of uploaded notes.
 */

package com.nmims.madproj.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

/**
 * Stores the original note text (if provided) and extracted text from OCR along with timestamp.
 */
@Entity(tableName = "uploaded_notes")
public class UploadedNote {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String noteText; // Optional: manual input or quick notes

    private String extractedText; // Result from OCR

    private long timestamp;

    public UploadedNote() {
        this.timestamp = System.currentTimeMillis();
    }

    @Ignore
    public UploadedNote(String noteText, String extractedText, long timestamp) {
        this.noteText = noteText;
        this.extractedText = extractedText;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}


