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
        Cursor cursor = database.rawQuery("select t.tag_id, tag_name, count(tag_name) from question_tag qt inner join tags t on qt.tag_id = t.tag_id group by t.tag_id, tag_name having t.enable= 1 " , null);
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
    public List<Tag> getTagState() {
        List<Tag> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("select tag_id, tag_name, enable from tags", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Tag tag = new Tag();
            tag.setId(cursor.getInt(0));
            tag.setName(cursor.getString(1));
            tag.setState(cursor.getInt(2));
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
    public void FillQuestionsAccordingLevel(List<String> tag_id, int total_question_num, List<Question> hardList,
                                                     List<Question>normalList, List<Question> easyList){

        int num_tag_appear = total_question_num / tag_id.size();

        for(int i = 0 ; i < tag_id.size(); i++) {
            String value = tag_id.get(i);
            if(i == (tag_id.size() - 1)){
                num_tag_appear = total_question_num - num_tag_appear * i;
            }
            Cursor cursor = database.rawQuery("select questions.question_id, question_text, level " +
                    "from questions inner join question_tag " +
                    "on questions.question_id = question_tag.question_id " +
                    "where tag_id = "+ value + " and level = 3 order by random() limit " + num_tag_appear , null);

            cursor.moveToFirst();
            int hard_question_according_tag = 0;

            while (!cursor.isAfterLast()) {
                hard_question_according_tag++;
                Question q = new Question();
                q.setId(cursor.getInt(0));
                q.setText(cursor.getString(1));
                q.setLevel(cursor.getInt(2));
                hardList.add(q);
                cursor.moveToNext();
            }
            cursor.close();

            if(hard_question_according_tag < num_tag_appear) {
                Cursor normalcursor = database.rawQuery("select questions.question_id, question_text, level " +
                        "from questions inner join question_tag " +
                        "on questions.question_id = question_tag.question_id " +
                        "where tag_id = " + value + " and level = 2 order by random() limit " + (num_tag_appear - hard_question_according_tag), null);

                normalcursor.moveToFirst();
                int normal_question_according_tag = 0;

                while (!normalcursor.isAfterLast()) {
                    normal_question_according_tag++;

                    Question q = new Question();
                    q.setId(normalcursor.getInt(0));
                    q.setText(normalcursor.getString(1));
                    q.setLevel(normalcursor.getInt(2));
                    normalList.add(q);
                    normalcursor.moveToNext();
                }
                normalcursor.close();


                if (hard_question_according_tag + normal_question_according_tag < num_tag_appear) {
                    Cursor easycursor = database.rawQuery("select questions.question_id, question_text, level " +
                            "from questions inner join question_tag " +
                            "on questions.question_id = question_tag.question_id " +
                            "where tag_id = " + value + " and level = 1 order by random() limit " + (num_tag_appear - hard_question_according_tag- normal_question_according_tag), null);

                    easycursor.moveToFirst();
                    int easy_question_according_tag = 0;

                    while (!easycursor.isAfterLast()) {
                        easy_question_according_tag ++;
                        Question q = new Question();
                        q.setId(easycursor.getInt(0));
                        q.setText(easycursor.getString(1));
                        q.setLevel(easycursor.getInt(2));
                        easyList.add(q);
                        easycursor.moveToNext();
                    }
                    easycursor.close();
                }
            }
        }

    }
    public List<Question> getQuestions(List<String> tag_id, int total_question_num){
        List<Question> list = new ArrayList<>();

        int num_tag_appear = total_question_num / tag_id.size();

        for(int i = 0 ; i < tag_id.size(); i++) {
            String value = tag_id.get(i);
            if(i == (tag_id.size() - 1)){
                num_tag_appear = total_question_num - num_tag_appear * i;
            }
            Cursor cursor = database.rawQuery("select questions.question_id, question_text, level " +
                    "from questions inner join question_tag " +
                    "on questions.question_id = question_tag.question_id " +
                    "where tag_id = "+ value + " order by random() limit " + num_tag_appear , null);

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

    public void updateTagState(List<Tag> taglist){

        for(int i = 0 ; i < taglist.size(); i++){
            String tag_id = ""+ taglist.get(i).getId();
            String state = ""+ (taglist.get(i)).getState();
            ContentValues cv = new ContentValues();
            cv.put("enable", state );
            database.update("tags",cv,"tag_id=?",new String[]{tag_id});
        }
    }

}