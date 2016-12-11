package we.sharediary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.newim.bean.BmobIMMessage;
import we.sharediary.R;
import we.sharediary.base.Constants;
import we.sharediary.util.Util;

/**
 * Created by zhanghao on 2016/11/23.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{

    private Context mContext;
    private List<BmobIMMessage> mMessageList = new ArrayList<>();
    private String uid;
    private static final int OTHER_TYPE = 1, MINE_TYPE = 0;
    private String objectName = "";

    public ChatAdapter(Context context) {
//        mMessageList = messageList;
        mContext = context;
        uid = (String) Util.readPreferences(Constants.USER_OBJECTID, 0);
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        mContext = parent.getContext();
        View root = null;
        if (viewType == MINE_TYPE) {
            root = LayoutInflater.from(mContext).inflate(R.layout.adapter_mine_chat, parent, false);
        }else if (viewType == OTHER_TYPE){
            root = LayoutInflater.from(mContext).inflate(R.layout.adapter_chat, parent, false);
        }
        ChatViewHolder holder = new ChatViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        BmobIMMessage message = mMessageList.get(position);
        holder.tvMsg.setText(message.getContent());
        holder.tvObject.setText(objectName);
//        if (message.getBmobIMConversation() != null) {
//            holder.ivHeader.setImageURI(Uri.parse(message.getBmobIMConversation().getConversationIcon()));
//        }
    }

    @Override
    public int getItemViewType(int position) {
        BmobIMMessage message = mMessageList.get(position);
        if (message.getFromId().equals(uid)) {
            objectName = "WO";
            return MINE_TYPE;
        }else {
            objectName = "TA";
            return OTHER_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList==null?0:mMessageList.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{
        public SimpleDraweeView ivHeader;
        public TextView tvMsg;
        public TextView tvObject;
        public ChatViewHolder(View itemView) {
            super(itemView);
            ivHeader = (SimpleDraweeView) itemView.findViewById(R.id.iv_header);
            tvMsg = (TextView) itemView.findViewById(R.id.tv_msg);
            tvObject = (TextView) itemView.findViewById(R.id.tv_chat_object);
        }
    }

    public void addMessages(List<BmobIMMessage> messages) {
        mMessageList.addAll(0, messages);
        notifyDataSetChanged();
    }

    public void addMessage(BmobIMMessage message) {
        mMessageList.addAll(Arrays.asList(message));
        notifyDataSetChanged();
    }
}
