package com.zalivka.commons.utils.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.viewpagerindicator.CirclePageIndicator;
import com.zalivka.commons.R;

public abstract class AbstractHelpDialog extends DialogFragment {

    protected View mRoot;

    protected ViewPager mPager;

    protected FragmentPagerAdapter mAdapter;

    protected Button mSkip;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.abstract_tutorial, null);
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mAdapter = createAdapter(getChildFragmentManager());
        mPager = (ViewPager)mRoot.findViewById(R.id.pager2);
        mPager.setAdapter(mAdapter);
        CirclePageIndicator indicator = (CirclePageIndicator)mRoot.findViewById(R.id.landing_tab_indicator);
        indicator.setViewPager(mPager);
        mPager.setOffscreenPageLimit(3);

        mSkip = (Button) mRoot.findViewById(R.id.tut_skip);
        mSkip.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dial = super.onCreateDialog(savedInstanceState);
        dial.setTitle(getTitle());
        return dial;
    }

    public abstract FragmentPagerAdapter createAdapter(FragmentManager manager);

    public String getTitle() {return "";};

}
