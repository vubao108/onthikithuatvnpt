package com.ttcntt.android.onthivnpt;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vuth1 on 14/03/2018.
 */

public class Question implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
    public Question(Parcel in){
        this.id = in.readInt();
        this.text = in.readString();
        this.level = in.readInt();
        this.true_question_text = in.readString();
        this.learned = in.readInt();
        this.appear_count = in.readInt();

    }
    public Question(){

    }


    @Override
    public String toString() {
        return "id " + id + ": level :"+level+ " text " + text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.text);
        parcel.writeInt(this.level);
        parcel.writeString(this.true_question_text);
        parcel.writeInt(this.appear_count);
        parcel.writeInt(this.learned);


    }

    private int id;
    private String text;
    private int level ;
    private int learned;

    private String true_question_text;

    private int appear_count;


    public int getLearned() {
        return learned;
    }

    public void setLearned(int learned) {
        this.learned = learned;
    }

    public int getAppear_count() {
        return appear_count;
    }

    public void setAppear_count() {
        this.appear_count = this.appear_count + 1;
    }


    public boolean isRemain(){
        return  this.level - this.appear_count > 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;

    }



    public String getTrue_question_text() {
        return true_question_text;
    }

    public void setTrue_question_text(String true_question_text) {
        this.true_question_text = true_question_text;
    }
}
