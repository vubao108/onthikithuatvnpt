package com.ttcntt.android.onthivnpt;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity {
    private final static String CURRENT_QUESTION_INDEX = "Current question index";
    private final static String NORMAL_LIST_QUESTION = "normal list question";
    private final static String EASY_LIST_QUESTION = "easy list question";
    private final static String HARD_LIST_QUESTION = "hard list question";
    private final static String LEVEL_VISIABLE = "level visiable";
    private final static String NUM_QUESTION = "num question";
    private final static String LEARNED_QUESTION = "learned question";
    private final static String TRUE_NUM = "true num";
    private final static String TOTAL_NUM = "total num";

    private RadioButton answer1;
    private TextView question;
    private RadioButton answer2;
    private RadioButton answer3;
    private RadioButton answer4;
    private RadioGroup groupRadio;
    private Question currentQuestion;
    private Button showAnswerButton;
    private Button easyButton, normalButton, hardButton;
    private TextView state;
    private  int question_index;
    private  boolean level_visible ;
    private int all_question_num ;
    private int learned_num;
    private TextView textSummary;
    private int true_answer_num;
    private int total_answer_num;

    //private int current_true_answer_index ;


    private List<Question> hardList, normalList, easyList;

    public static final String QUESTION = "question_activity" ;
    public static final String TOTAL_QUESTION_APPEAR = "total question to appear";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        question = (TextView)findViewById(R.id.id_main_question);
        answer1 = (RadioButton)findViewById(R.id.a1);
        answer2 = (RadioButton) findViewById(R.id.a2);
        answer3 = (RadioButton) findViewById(R.id.a3);
        answer4 = (RadioButton) findViewById(R.id.a4);
        groupRadio = (RadioGroup) findViewById(R.id.radioGroup);
        showAnswerButton = (Button)findViewById(R.id.check_button);
        normalButton = (Button)findViewById(R.id.button_normal);
        easyButton = (Button)findViewById(R.id.button_easy);
        hardButton = (Button)findViewById(R.id.button_hard);
        state = (TextView)findViewById(R.id.state);
        textSummary = (TextView)findViewById(R.id.summary);


        if(savedInstanceState == null) {
            hardList = new ArrayList<>();
            normalList = new ArrayList<>();
            easyList = new ArrayList<>();
            String ids = getIntent().getStringExtra(QUESTION);
            int total_question_to_appear = getIntent().getIntExtra(QuestionActivity.TOTAL_QUESTION_APPEAR, 100);
            String[] id_arr = ids.split(";");
            List<String> id_list = new ArrayList<>();
            for (int i = 0; i < id_arr.length; i++) {
                id_list.add(id_arr[i]);
            }
            DatabaseAccess database = DatabaseAccess.getInstance(getApplicationContext());
            database.open();
            database.FillQuestionsAccordingLevel(id_list,total_question_to_appear,hardList,normalList,easyList);
            all_question_num = hardList.size() + normalList.size() + easyList.size();


            question_index = getRandomIndex();






        }else{
            question_index = savedInstanceState.getInt(CURRENT_QUESTION_INDEX);
            hardList = savedInstanceState.getParcelableArrayList(HARD_LIST_QUESTION);
            normalList = savedInstanceState.getParcelableArrayList(NORMAL_LIST_QUESTION);
            easyList = savedInstanceState.getParcelableArrayList(EASY_LIST_QUESTION);
            level_visible = savedInstanceState.getBoolean(LEVEL_VISIABLE);
            all_question_num = savedInstanceState.getInt(NUM_QUESTION);
            learned_num = savedInstanceState.getInt(LEARNED_QUESTION);
            total_answer_num = savedInstanceState.getInt(TOTAL_NUM);
            true_answer_num = savedInstanceState.getInt(TRUE_NUM);


        }
        if(getQuestionList().size() >0) {
            currentQuestion = getQuestionList().get(question_index);
            setQuestionAnswer();

            setLevelVisiable(level_visible);
        } else{
            nextQuestion(); // reseet ui
        }
        textSummary.setText("Đã học: " + learned_num + "/" + all_question_num  + "\n" + "Điểm số: " + true_answer_num + "/" + total_answer_num);


        showAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton checked_Button = (RadioButton)findViewById(groupRadio.getCheckedRadioButtonId());
                total_answer_num = total_answer_num + 1;
                if (checked_Button != null && currentQuestion.getTrue_question_text() != null) {
                    if (currentQuestion.getTrue_question_text().equals(checked_Button.getText())) {
                        state.setText("Đúng rồi");
                        true_answer_num = true_answer_num + 1;

                    } else {
                        state.setText("Sai rồi");

                    }
                }
              //  updateCorrectAnswer();

                setLevelVisiable(true);
                groupRadio.setEnabled(false);
                currentQuestion.setAppear_count(); // tang len 1
                if(currentQuestion.getLearned() ==0 ) {
                    currentQuestion.setLearned(1);
                    learned_num = learned_num + 1;

                }
                textSummary.setText("Đã học: " + learned_num + "/" + all_question_num  + "\n" + "Điểm số: " + true_answer_num + "/" + total_answer_num);


            }
        });





        normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseAccess database = DatabaseAccess.getInstance(getApplicationContext());
                database.open();
                database.updateNormal(currentQuestion.getId());
                database.close();



                processLevel(2);

                /*
                question_index = getRandomIndex();
                currentQuestion = questionList.get(question_index);
                resetUI();
                setQuestionAnswer();
                */
                nextQuestion();
            }
        });
        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseAccess database = DatabaseAccess.getInstance(getApplicationContext());
                database.open();
                database.updateEasy(currentQuestion.getId());
                database.close();

                processLevel(1);

                nextQuestion();
                /*
                question_index = getRandomIndex();
                currentQuestion = questionList.get(question_index);
                resetUI();
                setQuestionAnswer();
                */
            }
        });

        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseAccess database = DatabaseAccess.getInstance(getApplicationContext());
                database.open();
                database.updateHard(currentQuestion.getId());
                database.close();

                processLevel(3);


               nextQuestion();
            }
        });


    }
    private void processLevel(int level){
        currentQuestion.setLevel(level);
        if(currentQuestion.getAppear_count() == 1) {
            if (hardList.size() > 0 ) {

                easyList.add(currentQuestion);

                hardList.remove(question_index);
            } else if (normalList.size() > 0) {

                easyList.add(currentQuestion);

                normalList.remove(question_index);
            }
        }

            if(!currentQuestion.isRemain()){

                int lasIndexInEasy = easyList.size() - 1;
                if(currentQuestion.getId() == easyList.get(lasIndexInEasy).getId() ){
                    easyList.remove(lasIndexInEasy);
                }else{
                    easyList.remove(question_index);
                }

            }

    }
    private List<Question> getQuestionList(){
        if(hardList.size() > 0 ){
            return hardList;
        }else if(normalList.size() >0 ){
            return normalList;
        }else{
            return easyList;
        }

    }
    /*
    private void setAppearCount(int level_num){
        currentQuestion.setLevel(level_num);

        if(!currentQuestion.isRemain()){
            getQuestionList().remove(question_index);
        }
    }
    */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_QUESTION_INDEX, question_index);
        outState.putParcelableArrayList(HARD_LIST_QUESTION, (ArrayList)hardList);
        outState.putParcelableArrayList(EASY_LIST_QUESTION, (ArrayList)easyList);
        outState.putParcelableArrayList(NORMAL_LIST_QUESTION, (ArrayList)normalList);
        outState.putBoolean(LEVEL_VISIABLE, level_visible);
        outState.putInt(NUM_QUESTION, all_question_num);
        outState.putInt(LEARNED_QUESTION, learned_num);
        outState.putInt(TOTAL_NUM, total_answer_num);
        outState.putInt(TRUE_NUM, true_answer_num);
    }

    private void updateCorrectAnswer(){
        if (answer1.getText().equals(currentQuestion.getTrue_question_text())){
            answer1.setBackgroundColor(Color.YELLOW);
        }else if (answer2.getText().equals(currentQuestion.getTrue_question_text())){
            answer2.setBackgroundColor(Color.YELLOW);
        }else if (answer3.getText().equals(currentQuestion.getTrue_question_text())){
            answer3.setBackgroundColor(Color.YELLOW);
        }else if((answer4.getText().equals(currentQuestion.getTrue_question_text()))) {
            answer4.setBackgroundColor(Color.YELLOW);
        }else{
            answer1.setBackgroundColor(Color.YELLOW);
            answer2.setBackgroundColor(Color.YELLOW);
            answer3.setBackgroundColor(Color.YELLOW);
            answer4.setBackgroundColor(Color.YELLOW);
            state.setText("chưa có đáp án");
        }
    }
    private void mergeAnswer(List<Answer> answers){
            int num_abvoe = answers.size();
            if(num_abvoe <=3){
                Log.i("info","3");
            }
            Random r = new Random();
            int index1 = r.nextInt(num_abvoe) ;
            int index2, index3, index4;
            answer1.setText(answers.get(index1).getText());
            do {
                index2 = r.nextInt(num_abvoe);

            }while (index2 == index1);
            answer2.setText(answers.get(index2).getText());


            if(answers.size() >= 3) {
                do {
                    index3 = r.nextInt(num_abvoe);

                }while (index3 == index1 || index3 == index2);
                answer3.setText(answers.get(index3).getText());

                if(answers.size() >=4) {
                    do {
                        index4 = r.nextInt(num_abvoe);

                    }while (index4 == index3 || index4 == index2 || index4 == index1);
                    answer4.setText(answers.get(index4).getText());
                }else{
                    answer4.setText("");
                }

            }else {
                answer3.setText("");
                answer4.setText("");
            }




    }

    private void resetUI(){


        groupRadio.clearCheck();
        answer1.setBackgroundResource(R.drawable.back);
        answer2.setBackgroundResource(R.drawable.back);
        answer3.setBackgroundResource(R.drawable.back);
        answer4.setBackgroundResource(R.drawable.back);

        setLevelVisiable(false);

        state.setText("");

    }

    private void setLevelVisiable(boolean isVisible){
        level_visible = isVisible;
        if(!isVisible){
            easyButton.setVisibility(View.INVISIBLE);
            normalButton.setVisibility(View.INVISIBLE);
            hardButton.setVisibility(View.INVISIBLE);
            showAnswerButton.setVisibility(View.VISIBLE);
        }else {
            easyButton.setVisibility(View.VISIBLE);
            normalButton.setVisibility(View.VISIBLE);
            hardButton.setVisibility(View.VISIBLE);
            showAnswerButton.setVisibility(View.INVISIBLE);
            updateCorrectAnswer();
        }
    }
    private void setQuestionAnswer(){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        List<Answer> answerList = databaseAccess.getAnswer(currentQuestion);
        question.setText(currentQuestion.getText());
        mergeAnswer(answerList);
    }
    private int getRandomIndex(){
        Random random = new Random();
        return random.nextInt(getQuestionList().size());

    }
    private  void nextQuestion(){

        if (getQuestionList().size() > 0){
            question_index = getRandomIndex();
            currentQuestion = getQuestionList().get(question_index);
            resetUI();
            setQuestionAnswer();
        } else {
            resetUI();
            state.setText("Xong hết rồi");
            question.setText("Xong phiên học ,xem kết quả ở dưới\n"+
                    "Bấm nút Back để về menu chính  !!!");
            groupRadio.setVisibility(View.INVISIBLE);
            showAnswerButton.setVisibility(View.INVISIBLE);
        }

    }

}
