package com.nmims.madproj.activities;

import android.os.Bundle;
import android.util.Base64;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nmims.madproj.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ModelViewerActivity extends AppCompatActivity {

    private WebView webView3D;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_viewer);

        webView3D = findViewById(R.id.webView3D);

        WebSettings webSettings = webView3D.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        webView3D.setWebViewClient(new WebViewClient());

        String topic = getIntent().getStringExtra("topic");
        String modelUrl = getIntent().getStringExtra("modelUrl");

        if (topic == null || topic.trim().isEmpty()) {
            topic = "Odontogenic 3D Model";
        }

        if (modelUrl == null || modelUrl.trim().isEmpty()) {
            modelUrl = "https://modelviewer.dev/shared-assets/models/Astronaut.glb";
        }

        String finalModelSource = modelUrl;

        if (!modelUrl.startsWith("http")) {
            String base64Model = readAssetAsBase64(modelUrl);

            if (base64Model != null && !base64Model.isEmpty()) {
                finalModelSource = "data:model/gltf-binary;base64," + base64Model;
            } else {
                finalModelSource = "https://modelviewer.dev/shared-assets/models/Astronaut.glb";
            }
        }

        String html = createModelViewerHtml(topic, finalModelSource);

        webView3D.loadDataWithBaseURL(
                "https://modelviewer.dev/",
                html,
                "text/html",
                "UTF-8",
                null
        );
    }

    private String readAssetAsBase64(String assetPath) {
        try {
            InputStream inputStream = getAssets().open(assetPath);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();

            byte[] modelBytes = outputStream.toByteArray();

            return Base64.encodeToString(modelBytes, Base64.NO_WRAP);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String createModelViewerHtml(String topic, String modelSource) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<script type='module' src='https://ajax.googleapis.com/ajax/libs/model-viewer/3.4.0/model-viewer.min.js'></script>" +
                "<style>" +
                "body { margin:0; background:#123943; color:white; font-family:Arial, sans-serif; text-align:center; }" +
                "h2 { margin-top:20px; font-size:24px; padding:0 12px; }" +
                "p { padding:0 18px; color:#e0e0e0; font-size:15px; line-height:1.5; }" +
                "model-viewer { width:100%; height:70vh; background:#123943; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h2>" + topic + "</h2>" +
                "<p>Rotate, zoom, and inspect the 3D model.</p>" +
                "<model-viewer " +
                "src='" + modelSource + "' " +
                "alt='" + topic + "' " +
                "camera-controls " +
                "auto-rotate " +
                "shadow-intensity='1'>" +
                "</model-viewer>" +
                "<p>This 3D model helps students understand dental and jaw anatomy interactively.</p>" +
                "</body>" +
                "</html>";
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}