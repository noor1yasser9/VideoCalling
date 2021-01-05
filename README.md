# Video Meeting

- Simple application. First the user creates a new account in the application using Firebase Auth (Sign Up), Second, user will log in to the application and We will store logged user information into shared preferences to handle auto-sign in so users don't need to enter email and password every time, and Final we display  list of registered users in the application except for the currently logged user because he cannot establish a call with himself.

# ✨ Features Project Android:
- 100% Kotlin
- MVVM architecture
- Single activity
- dataBinding 


# Used libraries
- ## Firebase Auth
- ### Before you begin
    1. If you haven't already, <a href="https://firebase.google.com/docs/android/setup?authuser=0">add Firebase to your Android project</a>.
    2. Using the <a href="https://firebase.google.com/docs/android/learn-more?authuser=0#bom">Firebase Android BoM</a>, declare the dependency for the Firebase Authentication Android library in your <b>module (app-level) Gradle file</b> (usually app/build.gradle).
        <br />
    ```groovy
        dependencies {
            // Import the BoM for the Firebase platform
            implementation platform('com.google.firebase:firebase-bom:26.2.0')

            // Declare the dependency for the Firebase Authentication library
            // When using the BoM, you don't specify versions in Firebase library dependencies
            implementation 'com.google.firebase:firebase-auth'
        }
     ```
     <br />
- ## Cloud Firestore

       ```groovy
        dependencies {
            implementation 'com.google.firebase:firebase-admin:7.1.0'
        }
        ```
- ## Firebase Cloud Messaging
    
           ```groovy
        dependencies {
            implementation 'com.google.firebase:firebase-messaging'
        }
        ```
        
     - A service that extends FirebaseMessagingService. This is required if you want to do any message handling beyond receiving notifications on apps in the background. To receive notifications in foregrounded apps, to receive data payload, to send upstream messages, and so on, you must extend this service.
     
          ```groovy
        <service
            android:name=".java.MyFirebaseMessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
             <!-- Set custom default icon. This is used when no icon is set for incoming notification messages.
             See README(https://goo.gl/l4GJaQ) for more. -->
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_icon"
            android:resource="@drawable/ic_stat_ic_notification" />
        <!-- Set color used with incoming notification messages. This is used when no color is set for the incoming
             notification message. See README(https://goo.gl/6BKBk7) for more. -->
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_color"
            android:resource="@color/colorAccent" />
        ```

- ## Android architecture components
    
         ```groovy
          dependencies {
                    // navigation
                    implementation "androidx.navigation:navigation-fragment-ktx:2.3.2"
                    implementation "androidx.navigation:navigation-ui-ktx:2.3.2"

                    // ViewModel &  LiveData
                    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0"
                    implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.2.0"
          }
        ```


- ## Retrofit

       ```groovy
              dependencies {
                   // Retrofit
                  implementation 'com.squareup.retrofit2:retrofit:2.9.0'
                  implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
                  implementation 'com.squareup.okhttp3:okhttp:4.9.0'
               }
       ```
  
- ## MultiDex

       ```groovy
              dependencies {
            //MultiDex
            implementation 'androidx.multidex:multidex:2.0.1'
               }
        ```
        
# For the Video Call I used SDK <a href ="https://jitsi.github.io/handbook/docs/dev-guide/dev-guide-android-sdk">JITSI</a>
   
   ### Use pre-build SDK artifacts/binaries
   - In your project, add the Maven repository https://github.com/jitsi/jitsi-maven-repository/raw/master/releases and the dependency org.jitsi.react:jitsi-meet-sdk into your build.gradle files.

        The repository typically goes into the build.gradle file in the root of your project:

               ```groovy
                    allprojects {
                        repositories {
                            google()
                            jcenter()
                            maven {
                                url "https://github.com/jitsi/jitsi-maven-repository/raw/master/releases"
                            }
                        }
                    }
                ```
        Dependency definitions belong in the individual module build.gradle files:        


               ```groovy    
                    dependencies {
                        // (other dependencies)
                        implementation ('org.jitsi.react:jitsi-meet-sdk:2.+') { transitive = true }
                    }
                ```
     - JitsiMeetConferenceOptions
         This object encapsulates all the options that can be tweaked when joining a conference.
       <br />
        Example:

               ```groovy    
                    JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                        .setServerURL(new URL("https://meet.jit.si"))
                        .setRoom("test123")
                        .setAudioMuted(false)
                        .setVideoMuted(false)
                        .setAudioOnly(false)
                        .setWelcomePageEnabled(false)
                        .build();
                ```


