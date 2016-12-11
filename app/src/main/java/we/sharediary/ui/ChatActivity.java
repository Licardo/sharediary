package we.sharediary.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.liuguangqiang.ipicker.internal.Logger;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.v3.exception.BmobException;
import we.sharediary.R;
import we.sharediary.adapter.ChatAdapter;
import we.sharediary.base.BaseActivity;
import we.sharediary.base.Constants;
import we.sharediary.result.RefreshEvent;
import we.sharediary.util.BusProviderUtil;
import we.sharediary.util.StringUtils;

public class ChatActivity extends BaseActivity implements View.OnClickListener, MessageListHandler {

    private RecyclerView mRecyclerView;
    private Button btnSend;
    private EditText etContent;
    private ChatAdapter mAdapter;
    private List<BmobIMMessage> messages;
    private BmobIMConversation mConversation;
    private Toolbar mToolbar;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        initData();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusProviderUtil.getBusInstance().post(new RefreshEvent(Constants.REFRESH_CHAT_LIST_CODE));
                finish();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_chat);
        etContent = (EditText) findViewById(R.id.et_content);
        btnSend = (Button) findViewById(R.id.btn_send);
        BusProviderUtil.getBusInstance().register(this);
    }

    private void initData() {
        messages = new ArrayList<>();
        mAdapter = new ChatAdapter(this);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        //在聊天页面的onCreate方法中，通过如下方法创建新的会话实例,这个obtain方法才是真正创建一个管理消息发送的会话
        mConversation = BmobIMConversation.obtain(BmobIMClient.getInstance(),
                (BmobIMConversation) getIntent().getSerializableExtra(Constants.BMOB_CONVERSION));
        getChatList();
        btnSend.setOnClickListener(this);

        mToolbar.setTitle(mConversation.getConversationTitle());

        //更新对话
        BmobIM.getInstance().updateConversation(mConversation);
    }

    /**
     * 获取聊天列表
     */
    private void getChatList() {
        mConversation.queryMessages(null, 10, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                if (e == null) {
                    if (null != list && list.size() > 0) {
//                        layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                        mAdapter.addMessages(list);
//                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    Snackbar.make(mRecyclerView, e.getErrorCode() + e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                BmobIMTextMessage textMessage = new BmobIMTextMessage();
                closeKey(etContent);
                String msg = etContent.getText().toString().trim();
                if (StringUtils.isEmpty(msg)) {
                    return;
                }
                textMessage.setContent(msg);
                //可设置额外信息
                Map<String,Object> map =new HashMap<>();
                map.put("level", "1");//随意增加信息
                textMessage.setExtraMap(map);
                mConversation.sendMessage(textMessage, listener);
                break;
        }
    }

    /**
     * 消息发送监听器
     */
    public MessageSendListener listener =new MessageSendListener() {

        @Override
        public void onProgress(int value) {
            super.onProgress(value);
            //文件类型的消息才有进度值
            Logger.i("onProgress："+value);
        }

        @Override
        public void onStart(BmobIMMessage msg) {
            super.onStart(msg);
            mAdapter.addMessage(msg);
            etContent.setText("");
            scrollToBottom();
        }

        @Override
        public void done(BmobIMMessage msg, BmobException e) {
            mAdapter.notifyDataSetChanged();
            etContent.setText("");
            scrollToBottom();
            if (e != null) {
//                toast(e.getMessage());
            }
        }
    };

    private void scrollToBottom() {
        mLayoutManager.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
    }

    private void closeKey(EditText etContent) {
        InputMethodManager imm = (InputMethodManager) etContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(etContent.getApplicationWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BmobIM.getInstance().addMessageListHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BmobIM.getInstance().removeMessageListHandler(this);
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        if (list == null) {
            return;
        }
        for (MessageEvent event : list) {
            if (event.getConversation().getConversationId().equals(mConversation.getConversationId())) {
//                messages.add(event.getMessage());
            }
        }
//        mAdapter.notifyDataSetChanged();
//        scrollToBottom();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BusProviderUtil.getBusInstance().post(new RefreshEvent(Constants.REFRESH_CHAT_LIST_CODE));
    }

    @Subscribe
    public void refresh(RefreshEvent event){
//        Log.e("ERROR", event.getCode()+event.getMsg());
        if (event.getCode() == Constants.REFRESH_CHAT_CODE) {
            String conversionId = event.getConversionId();
            if (conversionId.equals(mConversation.getConversationId())) {
                BmobIMMessage message = new BmobIMMessage();
                message.setContent(event.getMsg());
                message.setFromId(event.getTitle());
                mAdapter.addMessage(message);
                scrollToBottom();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProviderUtil.getBusInstance().unregister(this);
    }
}
