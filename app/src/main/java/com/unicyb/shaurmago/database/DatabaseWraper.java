package com.unicyb.shaurmago.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mafio on 26.05.2017.
 */

public class DatabaseWraper {
    private DatabaseReference mDatabase;


    public DatabaseWraper() {
        mDatabase =  FirebaseDatabase.getInstance().getReference();

    }
}
