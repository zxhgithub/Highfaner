package com.highfaner.highfaner.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.highfaner.highfaner.R;
import com.hyphenate.easeui.ui.EaseBaseFragment;

/**
 * 项目名称：Highfaner
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/8/1 17:55
 * 修改人：Administrator
 * 修改时间：2016/8/1 17:55
 * 修改备注：
 */
public class SystemListFragmet extends EaseBaseFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_systemlist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setUpView() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
