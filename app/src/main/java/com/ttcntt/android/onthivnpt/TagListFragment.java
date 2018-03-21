package com.ttcntt.android.onthivnpt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by NHC on 12/02/2018.
 */

public class TagListFragment extends Fragment {
    public static final String TAG = "RecyclerView";
    public int MY_REQUEST_CODE = 108;

    private RecyclerView mTagRecyclerView;
    private TagAdapder mAdapter;
    private static List<Integer> list_current_display_view = new ArrayList();
    public static int ID = 0;
    private Button button_hoc, button_thi;
    private List<Tag> tag_id_list ;
    private Spinner spinner;
    private int total_question_to_appear;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_list, container, false);
        mTagRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mTagRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        spinner = (Spinner)view.findViewById(R.id.spinner);
        tag_id_list = new ArrayList<>();
        button_hoc = (Button)view.findViewById(R.id.hoc_button);
        button_thi = (Button)view.findViewById(R.id.button_thi);
        total_question_to_appear = 50;
        button_hoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tag_id_list.size() > 0) {
                    Log.i(TAG, "button thi clicked");
                    String ids = "";
                    for (Tag tag : tag_id_list) {
                        ids = ids + tag.getId()+ ";";
                    }
                    Intent intent = new Intent(getActivity(), QuestionActivity.class);
                    intent.putExtra(QuestionActivity.QUESTION, ids);
                    intent.putExtra(QuestionActivity.TOTAL_QUESTION_APPEAR,total_question_to_appear);
                    startActivity(intent);
                }else {
                    Toast.makeText(getContext(), "Phải chọn ít nhất 1 chủ đề ", Toast.LENGTH_LONG);
                }

            }
        });
        button_thi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tag_id_list.size() > 0) {
                    Log.i(TAG, "button thi clicked");
                    String ids = "";
                    for (Tag tag : tag_id_list) {
                        ids = ids + tag.getId() + ";";
                    }
                    Intent intent = new Intent(getActivity(), ThiQuestionActivity.class);
                    intent.putExtra(QuestionActivity.QUESTION, ids);
                    intent.putExtra(QuestionActivity.TOTAL_QUESTION_APPEAR,total_question_to_appear);
                    startActivity(intent);
                }else {
                    Toast.makeText(getContext(), "Phải chọn ít nhất 1 chủ đề ", Toast.LENGTH_LONG);
                }
            }
        });
        updateUI();

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
       Log.i(TAG, "onresume");
        // updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.tag_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.setting){
            Intent intent = new Intent(getActivity(), FilterActivity.class);
            startActivityForResult(intent, MY_REQUEST_CODE);


        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK){
            updateUI();
        }
    }

    private void updateUI(){
        DataLab dataLab = DataLab.get(getActivity());
        List<Tag> tags = dataLab.getTags();

        tag_id_list = new ArrayList<>();
        mAdapter = new TagAdapder(tags);
        mTagRecyclerView.setAdapter(mAdapter);

        ArrayAdapter<CharSequence>  spinAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_total_question, android.R.layout.simple_spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                   String item = adapterView.getItemAtPosition(i).toString();
                   try{
                       total_question_to_appear = Integer.parseInt(adapterView.getItemAtPosition(i).toString());

                   }catch (Exception e){
                        total_question_to_appear = 10000;
                   }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });
    }


    private class TagHolder extends RecyclerView.ViewHolder{
        public int id;
        private Tag mTag;
        private CheckBox mCheckBox;


        public TagHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_tag, parent, false ));
            id =  ++ID;
           mCheckBox = (CheckBox)itemView.findViewById(R.id.checkBox);
           mCheckBox.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Log.i(TAG, "clicked checkbox: " + mTag.getId() + " - " + mTag.getName());
                   if(mCheckBox.isChecked()){
                       Log.i(TAG, "checkedbox is checked");

                       tag_id_list.add(mTag);

                   }else {
                       Log.i(TAG, "checkbox not checked");
                       for(int i=0;i<tag_id_list.size();i++) {
                           if(tag_id_list.get(i).getId() == mTag.getId()) {
                               tag_id_list.remove(i);
                               break;
                           }
                       }
                   }

               }
           });


        }
        public void bind(Tag tag){
            mTag = tag;
            mCheckBox.setText(tag.getPosition() + "-" + tag.getName() + "(" + tag.getCount() + "câu)");
        }


    }
    private class TagAdapder extends RecyclerView.Adapter<TagHolder>{
        private List<Tag> mTags;
        public TagAdapder(List<Tag> tags){
            mTags = tags;
        }

        @Override
        public TagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i(TAG, "onCreateViewHolder viewType = " + viewType);
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            TagHolder crimeHolder = new TagHolder(layoutInflater, parent);
           // list_current_display_view.add(crimeHolder.id);
            return crimeHolder;
        }

        @Override
        public void onBindViewHolder(TagHolder holder, int position) {
            Log.i(TAG, "  onBindViewHolder position = " + position + " id " + holder.id);
            Log.i(TAG,"so luong viewholder : " + list_current_display_view.size());
            list_current_display_view.add(holder.id);
            print_currentId(10);
            Tag tag = mTags.get(position);
            tag.setPosition(position+1);
            holder.bind(tag);
        }
        private void print_currentId(int number){
            String str = "";
            for(int i = 0; i < number && number < list_current_display_view.size(); i++){
                str = str +  " " + list_current_display_view.get(list_current_display_view.size() - 1 - i ) ;

            }
            Log.i(TAG, "current id : "+str);
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "getItemCount");
            return mTags.size();
        }
    }
}
