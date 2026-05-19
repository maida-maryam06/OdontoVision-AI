# OdontovISION-AI - AI-Powered tutor Application

An Android quiz application with AI-powered question generation from notes, PDFs, and images.


Features

- Student and Faculty role-based login
- Student dashboard for learning and quiz practice
- Faculty dashboard for content upload and performance monitoring
- Faculty lecture PDF upload
- Student access to uploaded oral pathology notes
- AI chatbot for oral pathology questions
- AI-based MCQ generation from uploaded PDF/image content
- Preloaded oral pathology quizzes
- Quiz result screen
- Faculty-uploaded quiz solution support
- Student-wise performance report
- Quiz attempt history
- Interactive AR/3D learning module using GLB models
- Firebase Authentication and Firestore integration
- Room Database for local quiz storage

## Setup Instructions

### 1. Prerequisites
- Android Studio Hedgehog or newer
- Android SDK (minimum API 24)
- Java 11+

### 2. API Keys Configuration

1. Copy `Constants.java.template` to `app/src/main/java/com/nmims/madproj/utils/Constants.java`
2. Get your Gemini API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
3. Replace `YOUR_GEMINI_API_KEY_HERE` with your actual API key

```java
public static String GEMINI_API_KEY = "your-actual-api-key-here";
```

### 3. Firebase Setup (Optional)

The app works without Firebase, but for full functionality:

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
2. Enable Firestore Database and Authentication
3. Download `google-services.json` and place it in `app/` directory
4. Update Firestore security rules (see Firebase documentation)

### 4. Build and Run

```bash
./gradlew build
./gradlew installDebug
```

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/nmims/madproj/
│   │   │   ├── activities/      # UI Activities
│   │   │   ├── database/        # Room database (DAO, entities)
│   │   │   ├── models/          # Data models
│   │   │   ├── repository/      # Data repositories
│   │   │   ├── viewmodel/       # ViewModels (MVVM pattern)
│   │   │   ├── utils/           # Utilities (Constants, TextExtractor, etc.)
│   │   │   └── services/        # Background services
│   │   ├── res/                 # Resources (layouts, drawables, values)
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts         # App-level dependencies
├── build.gradle.kts             # Project-level config
└── settings.gradle.kts
```

## Key Components

### Activities
- **MainActivity**: Entry point with navigation
- **QuizSelectionActivity**: Category selection
- **QuizActivity**: Quiz taking interface
- **UploadNoteActivity**: Upload images/PDFs for quiz generation
- **ChatbotActivity**: AI chatbot interface
- **ResultActivity**: Quiz results display
- **StatsActivity**: Performance statistics
- **LoginActivity/RegisterActivity**: User authentication

### Core Features
- **Room Database**: Local storage for questions and quiz results
- **Firebase Integration**: Optional cloud sync
- **ML Kit OCR**: Text extraction from images
- **PDFBox**: PDF text extraction
- **Gemini API**: AI quiz generation and chatbot

## Dependencies

- AndroidX (AppCompat, Material Design)
- Room Database
- Firebase (Auth, Firestore)
- ML Kit Text Recognition
- Retrofit & OkHttp
- PDFBox Android

## Notes

- The app works in guest mode if Firebase is not configured
- Questions are stored locally in Room database
- Generated quizzes are temporary and cleared on new generation
- Preloaded questions are automatically seeded on first launch

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is for educational purposes.

