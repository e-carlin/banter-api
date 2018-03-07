package com.banter.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    
    public FirebaseConfig() throws IOException {
        FileInputStream firebaseServiceAccount =
                //TODO: can't use a file name like this. The JSON file should NOT be checked into src control
                new FileInputStream("C:\\Users\\evan.carlin\\Documents\\personal\\banter\\banter-api-2\\src\\main\\java\\com\\banter\\api\\config\\banter-firebase-admin.json");

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(firebaseServiceAccount))
                .setDatabaseUrl("https://banter-81a54.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(firebaseOptions);
    }

    @Bean
    public Firestore firestoreDb() {
        return FirestoreClient.getFirestore();
    }
}
