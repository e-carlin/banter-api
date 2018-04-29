package com.banter.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredential;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirestoreConfig {
    @Value("${firestore.database-url}")
    private String databaseUrl;

    public FirestoreConfig() throws IOException {

        String firebaseCreds = System.getenv("FIREBASE_CREDS");
        InputStream firebaseServiceAccount = new ByteArrayInputStream(firebaseCreds.getBytes(StandardCharsets.UTF_8));
//        FileInputStream firebaseServiceAccount =
//                //TODO: can't use a file name like this. The JSON file should NOT be checked into src control
//                new FileInputStream("banter-firebase-admin.json");

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(firebaseServiceAccount))
                .setDatabaseUrl(databaseUrl)
                .build();

        FirebaseApp.initializeApp(firebaseOptions);
    }

    @Bean
    public Firestore firestoreDb() {
        return FirestoreClient.getFirestore();
    }
}
