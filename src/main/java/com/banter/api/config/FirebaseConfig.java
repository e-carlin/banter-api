package com.banter.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {
    
    public FirebaseConfig() throws IOException {
        FileInputStream serviceAccount =
                //TODO: can't use a file name like this. The JSON file should NOT be checked into src control
                new FileInputStream("C:\\Users\\evan.carlin\\Documents\\personal\\banter\\banter-api-2\\src\\main\\java\\com\\banter\\api\\config\\banter-firebase-admin.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://banter-81a54.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);

        // Use the application default credentials
//        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(credentials)
//                .setProjectId(projectId)
//                .build();
//        FirebaseApp.initializeApp(options);
//
//        Firestore db = FirestoreClient.getFirestore();
    }
}
