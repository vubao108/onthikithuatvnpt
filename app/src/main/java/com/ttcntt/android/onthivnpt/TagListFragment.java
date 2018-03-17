package com.ttcntt.android.onthivnpt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NHC on 12/02/2018.
 */

public class TagListFragment extends Fragment {
    public static final String TAG = "RecyclerView";

    private RecyclerView mTagRecyclerView;
    private TagAdapder mAdapter;
    private static List<Integer> list_current_display_view = new ArrayList();
    public static int ID = 0;
    private Button button_hoc;
    private List<String> tag_id_list ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mTagRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mTagRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tag_id_list = new ArrayList<>();
        button_hoc = (Button)view.findViewById(R.id.hoc_button);
        //button_thi = (Button)view.findViewById(R.id.thi_button);
        button_hoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tag_id_list.size() > 0) {
                    Log.i(TAG, "button thi clicked");
                    String ids = "";
                    for (String value : tag_id_list) {
                        ids = ids + value + ";";
                    }
                    Intent intent = new Intent(getActivity(), QuestionActivity.class);
                    intent.putExtra(QuestionActivity.QUESTION, ids);
                    startActivity(intent);
                }else {
                    Toast.makeText(getContext(), "Phải chọn ít nhất 1 chủ đề ", Toast.LENGTH_LONG);
                }

            }
        });
        updateUI();

        return view;
    }
    private void updateUI(){
        DataLab dataLab = DataLab.get(getActivity());
        List<Tag> tags = dataLab.getTags();

        mAdapter = new TagAdapder(tags);
        mTagRecyclerView.setAdapter(mAdapter);

    }

    private class TagHolder extends RecyclerView.ViewHolder{
        public int id;
        private Tag mTag;
        private CheckBox mCheckBox;


        public TagHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_crime, parent, false ));
            id =  ++ID;
           mCheckBox = (CheckBox)itemView.findViewById(R.id.checkBox);
           mCheckBox.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Log.i(TAG, "clicked checkbox: " + mTag.getId() + " - " + mTag.getName());
                   if(mCheckBox.isChecked()){
                       Log.i(TAG, "checkedbox is checked");
                       tag_id_list.add(""+mTag.getId());

                   }else {
                       Log.i(TAG, "checkbox not checked");
                       tag_id_list.remove(""+mTag.getId());
                   }
                   for (String value : tag_id_list) {
                       Log.i(TAG, value);
                   }

               }
           });


        }
        public void bind(Tag tag){
            mTag = tag;
            mCheckBox.setText(tag.getId() + "-" + tag.getName() + "(" + tag.getCount() + "câu)");
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