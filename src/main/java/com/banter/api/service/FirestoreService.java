package com.banter.api.service;

import com.banter.api.model.document.ChatDocument;
import com.banter.api.model.document.TransactionDocument;
import com.google.cloud.firestore.*;
import com.google.firebase.database.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class FirestoreService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Firestore db;

    @Value("${firestore.collection.ref.chat}")
    String chatRef;

    public FirestoreService() {}

    public void startFirestoreListener() {
//        CollectionReference docRef = db.collection(chatRef); //TODO: Use this
        CollectionReference docRef = db.collection("chats");
        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirestoreException e) {
                if(e != null) {
                    logger.error("There was an error listening to changes in the firestore chats collection. E: "+e.getLocalizedMessage());
                    e.printStackTrace();
                }
                else if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {


                    for(ChatDocument chatDoc : queryDocumentSnapshots.toObjects(ChatDocument.class)) {
                        logger.debug("CHAT: "+chatDoc);
                        //TODO: I'm here. The problem i'm running into is whenever app server reboots all chats are processed again. Q out on S.O.
                    }
                }
            }
        });

    }
}