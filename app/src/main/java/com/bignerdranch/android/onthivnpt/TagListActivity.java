package com.bignerdranch.android.onthivnpt;

import android.support.v4.app.Fragment;

/**
 * Created by NHC on 12/02/2018.
 */

public class TagListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new TagListFragment();
    }

}
