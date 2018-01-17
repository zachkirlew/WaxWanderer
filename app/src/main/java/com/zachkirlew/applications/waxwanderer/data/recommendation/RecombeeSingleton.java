package com.zachkirlew.applications.waxwanderer.data.recommendation;



import java.io.Serializable;

public class RecombeeSingleton implements Serializable {

    private static volatile RecombeeClient sSoleInstance;

    //private constructor.
    private RecombeeSingleton(){

        //Prevent form the reflection api.
        if (sSoleInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static RecombeeClient getInstance(String db, String secretToken) {
        if (sSoleInstance == null) { //if there is no instance available... create new one
            synchronized (RecombeeSingleton.class) {
                if (sSoleInstance == null){
                    sSoleInstance = new RecombeeClient(db, secretToken);
                }
            }
        }
        return sSoleInstance;
    }

}