package com.ttcntt.android.onthivnpt;

/**
 * Created by vuth1 on 21/03/2018.
 */

import android.app.Activity;
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
import android.widget.Button;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by NHC on 12/02/2018.
 */

public class FilterListFragment extends Fragment {
    public static final String TAG = "RecyclerView";

    private RecyclerView mTagRecyclerView;
    private TagAdapder mAdapter;

    public static int ID = 0;
    private Button button_update;
    private List<Tag> tag_id_list ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_tag_list_fragment, container, false);
        mTagRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mTagRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



                updateUI();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.update_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.update_item){

            DataLab dataLab = DataLab.get(getActivity());
            dataLab.updateTagState(tag_id_list);
            Intent intent = new Intent();
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();


        }
        return true;
    }




    private void updateUI(){
        DataLab dataLab = DataLab.get(getActivity());
        tag_id_list = dataLab.getTagState();

        mAdapter = new TagAdapder(tag_id_list);
        mTagRecyclerView.setAdapter(mAdapter);


    }
    private  void setTagState(int stateNum, Tag tag){
        for(int i = 0; i < tag_id_list.size(); i++){
            if(tag_id_list.get(i).getId() == tag.getId()){
                tag_id_list.get(i).setState(stateNum);
                tag.setState(stateNum);
                break;
            }
        }
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
                        Log.i(TAG, "checkedbox is checked "+ mTag.getId());
                        setTagState(1, mTag);

                    }else {
                        Log.i(TAG, "checkbox not checked");
                        setTagState(0,mTag);

                    }


                }
            });


        }
        public void bind(Tag tag){
            mTag = tag;
            mCheckBox.setText(tag.getId() + "-" + tag.getName());
            if(mTag.getState() == 1){
                mCheckBox.setChecked(true);
            }
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
            TagHolder tagHolder = new TagHolder(layoutInflater, parent);
            // list_current_display_view.add(crimeHolder.id);
            return tagHolder;
        }

        @Override
        public void onBindViewHolder(TagHolder holder, int position) {
            Log.i(TAG, "  onBindViewHolder position = " + position + " id " + holder.id);


            Tag tag = mTags.get(position);
            holder.bind(tag);
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "getItemCount");
            return mTags.size();
        }
    }
}


