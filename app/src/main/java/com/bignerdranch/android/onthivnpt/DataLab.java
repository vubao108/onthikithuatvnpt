package com.bignerdranch.android.onthivnpt;

import android.content.Context;

import java.util.List;

/**
 * Created by NHC on 09/02/2018.
 */

public class DataLab {
    private static DataLab sDataLab;
    private Context mContext;
    private DatabaseAccess mDatabase;
    private List<Question> mQuestions;

    public static DataLab get(Context context){
        if(sDataLab == null){
            sDataLab = new DataLab(context);
        }
        return sDataLab;
    }
    private DataLab(Context context){
       mContext = context.getApplicationContext();
       mDatabase = DatabaseAccess.getInstance(mContext);
    }
    public List<Tag> getTags(){

        mDatabase.open();
        List<Tag> list_tag = mDatabase.getTags();
        mDatabase.close();

        return list_tag;
    }



}
