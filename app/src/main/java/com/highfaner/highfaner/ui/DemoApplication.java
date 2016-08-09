package com.highfaner.highfaner.ui;

import android.app.Application;
import android.content.BroadcastReceiver;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.util.EMLog;

import java.util.List;

public class DemoApplication extends Application{
    protected EMMessageListener messageListener = null;
    @Override
    public void onCreate() {
        super.onCreate();
        EaseUI.getInstance().init(this, null);
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        EMClient .getInstance().init(getApplicationContext(),options);
        EMClient.getInstance().setDebugMode(true);
        registerMessageListener();
    }

    /**
     * Global listener
     * If this event already handled by an activity, you don't need handle it again
     * activityList.size() <= 0 means all activities already in background or not in Activity Stack
     */
    protected void registerMessageListener() {
        messageListener = new EMMessageListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    MainActivity.refreshMsg();
                    // in background, do not refresh UI, notify it in notification bar
//                    if(!easeUI.hasForegroundActivies()){
//                        getNotifier().onNewMsg(message);
//                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS", "receive command message");
                    //get message body
                    EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action();//获取自定义action
                    //red packet code : 处理红包回执透传消息
//                    if(!easeUI.hasForegroundActivies()){
//                        if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION)){
//                            RedPacketUtil.receiveRedPacketAckMessage(message);
//                            broadcastManager.sendBroadcast(new Intent(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION));
//                        }
//                    }
//                    //end of red packet code
//                    //获取扩展属性 此处省略
//                    //maybe you need get extension of your message
//                    //message.getStringAttribute("");
//                    EMLog.d(TAG, String.format("Command：action:%s,message:%s", action,message.toString()));
                }
            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> messages) {
            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {

            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }
}
