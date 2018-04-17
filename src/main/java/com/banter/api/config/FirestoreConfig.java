package com.banter.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirestoreConfig {
    @Value("${firestore.database-url}")
    private String databaseUrl;

    public FirestoreConfig() throws IOException {

        FileInputStream firebaseServiceAccount =
                //TODO: can't use a file name like this. The JSON file should NOT be checked into src control
                new FileInputStream("/home/e-carlin/banter/banter-api/src/main/java/com/banter/api/config/banter-firebase-admin.json");

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
