package com.hfaufhreu.hjfeuio.ui.fragment.vip;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hfaufhreu.hjfeuio.R;
import com.hfaufhreu.hjfeuio.base.BaseMainFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Case By:黑金会员专区
 * package:com.hfaufhreu.hjfeuio.ui.fragment.vip
 * Author：scene on 2017/4/17 10:17
 */

public class BlackGlodVipFragment extends BaseMainFragment {
    private Unbinder unbinder;

    public static BlackGlodVipFragment newInstance() {
        Bundle args = new Bundle();
        BlackGlodVipFragment fragment = new BlackGlodVipFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_black_glod_vip, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
