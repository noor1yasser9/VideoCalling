# Video Meeting

- Simple application. First the user creates a new account in the application using Firebase Auth (Sign Up), Second, user will log in to the application and We will store logged user information into shared preferences to handle auto-sign in so users don't need to enter email and password every time, and Final we display  list of registered users in the application except for the currently logged user because he cannot establish a call with himself.

# ✨ Features Project Android:
- 100% Kotlin
- MVVM architecture
- Single activity
- dataBinding 


# Used libraries
- ## Firebase
- ### Before you begin
    1. If you haven't already, <a href="https://firebase.google.com/docs/android/setup?authuser=0">add Firebase to your Android project</a>.
    2. Using the <a href="https://firebase.google.com/docs/android/learn-more?authuser=0#bom">Firebase Android BoM</a>, declare the dependency for the Firebase Authentication Android library in your <b>module (app-level) Gradle file</b> (usually app/build.gradle).
    
    ```groovy
dependencies {
    // Import the BoM for the Firebase platform
    implementation platform('com.google.firebase:firebase-bom:26.2.0')

    // Declare the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation 'com.google.firebase:firebase-auth'
}
```

