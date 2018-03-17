package com.ttcntt.android.onthivnpt;

/**
 * Created by vuth1 on 13/03/2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to avoid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * Read all quotes from the database.
     *
     * @return a List of quotes
     */
    public List<Tag> getTags() {
        List<Tag> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("select t.tag_id, tag_name, count(tag_name) from question_tag qt inner join tags t on qt.tag_id = t.tag_id group by t.tag_id, tag_name", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Tag tag = new Tag();
            tag.setId(cursor.getInt(0));
            tag.setName(cursor.getString(1));
            tag.setCount(cursor.getInt(2));
            list.add(tag);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }
    public List<Question> getQuestions(List<String> tag_id){
        List<Question> list = new ArrayList<>();

        for(String value:tag_id) {
            Cursor cursor = database.rawQuery("select questions.question_id, question_text, level " +
                    "from questions inner join question_tag " +
                    "on questions.question_id = question_tag.question_id " +
                    "where tag_id = "+ value, null);

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                Question q = new Question();
                q.setId(cursor.getInt(0));
                q.setText(cursor.getString(1));
                q.setLevel(cursor.getInt(2));
                list.add(q);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }
    public List<Answer> getAnswer(Question question){
        List<Answer> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("select answers.answer_id, answers.answer_text, question_answer.state " +
                "from answers inner join question_answer " +
                "on answers.answer_id = question_answer.answer_id " +
                "where question_answer.question_id =" + question.getId(),null );
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Answer a = new Answer();
            a.setId(cursor.getInt(0));
            a.setText(cursor.getString(1));
            if(cursor.getInt(2)==1) {
                a.setState(true);
                question.setTrue_question_text(a.getText());
            }
            list.add(a);
            cursor.moveToNext();
        }
        cursor.close();
        return  list;
    }
    public void updateEasy(int questionId){
        ContentValues cv = new ContentValues();
        cv.put("level",1);
        database.update("questions",cv, "question_id=?",new String[]{""+questionId});
    }
    public void updateHard(int questionId){
        ContentValues cv = new ContentValues();
        cv.put("level",3);
        database.update("questions",cv, "question_id=?",new String[]{""+questionId});
    }
    public void updateNormal(int questionId){
        ContentValues cv = new ContentValues();
        cv.put("level",2);
        database.update("questions",cv, "question_id=?",new String[]{""+questionId});
    }
}