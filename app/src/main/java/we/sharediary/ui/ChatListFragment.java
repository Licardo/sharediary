package we.sharediary.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.exception.BmobException;
import we.sharediary.R;
import we.sharediary.adapter.ChatListAdapter;
import we.sharediary.base.BaseFragment;
import we.sharediary.base.Constants;
import we.sharediary.listener.ClickItemChatListener;
import we.sharediary.result.RefreshEvent;
import we.sharediary.util.BusProviderUtil;

public class ChatListFragment extends BaseFragment implements ClickItemChatListener{

    private RecyclerView mRecyclerView;
    private ChatListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat_list, container, false);
        if (root instanceof RecyclerView) {
            mRecyclerView = (RecyclerView) root;
            BusProviderUtil.getBusInstance().register(this);
            initData();
        }
        return root;
    }

    private void initData(){
        List<BmobIMConversation> conversations = BmobIM.getInstance().loadAllConversation();
        mAdapter = new ChatListAdapter(conversations, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClickItemListener(BmobIMConversation conversation) {
        BmobIMUserInfo userInfo = new BmobIMUserInfo();
        userInfo.setUserId(conversation.getConversationId());
        userInfo.setName(conversation.getConversationTitle());
        userInfo.setAvatar(conversation.getConversationIcon());
        BmobIM.getInstance().startPrivateConversation(userInfo, new ConversationListener() {
            @Override
            public void done(BmobIMConversation bmobIMConversation, BmobException e) {
                if (e == null) {
                    //进入聊天界面
                    Intent intent = new Intent();
                    intent.setClass(ChatListFragment.this.getActivity(), ChatActivity.class);
                    intent.putExtra(Constants.BMOB_CONVERSION, bmobIMConversation);
                    startActivity(intent);
                }else {
                    Snackbar.make(mRecyclerView, e.getErrorCode()+e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Subscribe
    public void refresh(RefreshEvent event){
        if (event.getCode() == Constants.REFRESH_CHAT_LIST_CODE) {
            initData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProviderUtil.getBusInstance().unregister(this);
    }
}
