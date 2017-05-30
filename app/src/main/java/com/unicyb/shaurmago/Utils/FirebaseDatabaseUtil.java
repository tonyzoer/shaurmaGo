package com.unicyb.shaurmago.Utils;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mafio on 29.05.2017.
 */

public class FirebaseDatabaseUtil {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}
