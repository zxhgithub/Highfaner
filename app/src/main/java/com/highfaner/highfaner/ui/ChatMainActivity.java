package com.highfaner.highfaner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.highfaner.highfaner.Fragment.PrivateListFragment;
import com.highfaner.highfaner.Fragment.SystemListFragmet;
import com.highfaner.highfaner.R;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.ui.EaseContactListFragment;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 项目名称：Highfaner
 * 类描述：
 * 创建人：zhangxh
 * 创建时间：2016/8/1 17:41
 * 修改人：Administrator
 * 修改时间：2016/8/1 17:41
 * 修改备注：
 */
public class ChatMainActivity extends EaseBaseActivity {
    @InjectView(R.id.btn_back)
    LinearLayout btnBack;
    @InjectView(R.id.tv_content_left)
    TextView tvContentLeft;
    @InjectView(R.id.tv_content_right)
    TextView tvContentRight;
    @InjectView(R.id.fragment_container)
    RelativeLayout fragmentContainer;
    private SystemListFragmet systemListFragmet;
    private PrivateListFragment privateListFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;
    private  EaseContactListFragment contactListFragment;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat_main);
        ButterKnife.inject(this);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        systemListFragmet = new SystemListFragmet();
        privateListFragment = new PrivateListFragment();
        contactListFragment = new EaseContactListFragment();
        contactListFragment.setContactsMap(getContacts());
        fragments = new Fragment[] { privateListFragment, contactListFragment};
        // add and show first fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, privateListFragment)
                .add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(privateListFragment)
                .commit();
        contactListFragment.setContactListItemClickListener(new EaseContactListFragment.EaseContactListItemClickListener() {

            @Override
            public void onListItemClicked(EaseUser user) {
                startActivity(new Intent(ChatMainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));
            }
        });
    }

    /**
     * 设置监听器
     * @param view
     */
    @OnClick({R.id.btn_back,R.id.tv_content_left,R.id.tv_content_right})
    public  void  setOnClick(View view){
        if(view.getId() == R.id.btn_back){

        } else if(view.getId() == R.id.tv_content_left){
                index = 0;
            tvContentLeft.setTextColor(getResources().getColor(R.color.colorWhite));
            tvContentRight.setTextColor(getResources().getColor(R.color.colorTitleBar2));
            changeFragment();
        } else if (view.getId() == R.id.tv_content_right){
                index = 1;
            tvContentRight.setTextColor(getResources().getColor(R.color.colorWhite));
            tvContentLeft.setTextColor(getResources().getColor(R.color.colorTitleBar2));
            changeFragment();
        }
    }

    /**
     * 切换Fragment
     */
    private void changeFragment(){
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        currentTabIndex = index;
    }

    /**
     * prepared users, password is "123456"
     * you can use these user to test
     * @return
     */
    private Map<String, EaseUser> getContacts(){
        Map<String, EaseUser> contacts = new HashMap<String, EaseUser>();
//        for(int i = 1; i <= 10; i++){
        EaseUser user = new EaseUser("zhangq");
        EaseUser user1 = new EaseUser("wangwu");
        EaseUser user2 = new EaseUser("lisi");
        EaseUser user3 = new EaseUser("zhangxh");
        EaseUser user4 = new EaseUser("daseuitest");
        EaseUser user5 = new EaseUser("easeuitest");
        EaseUser user6 = new EaseUser("faseuitest");
        EaseUser user7 = new EaseUser("gaseuitest");

        contacts.put("zhangq", user);
        contacts.put("wangwu", user1);
        contacts.put("lisi", user2);
        contacts.put("zhangxh", user3);
        contacts.put("daseuitest", user5);
        contacts.put("easeuitest", user4);
        contacts.put("faseuitest", user6);
        contacts.put("gaseuitest", user7);

//        }
        return contacts;
    }

}
