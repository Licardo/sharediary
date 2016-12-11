package we.sharediary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import we.sharediary.R;
import we.sharediary.listener.ClickItemChatListener;

/**
 * Created by zhanghao on 2016/11/23.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder>{

    private Context mContext;
    private List<BmobIMConversation> mList;
    private ClickItemChatListener mListener;

    public ChatListAdapter(List<BmobIMConversation> list, ClickItemChatListener listener) {
        mList = list;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View root = LayoutInflater.from(mContext).inflate(R.layout.adapter_chat_list, parent, false);
        ViewHolder holder = new ViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BmobIMConversation conversation = mList.get(position);
//        holder.ivHeader.setImageURI(Uri.parse(conversation.getConversationIcon()));
        holder.tvName.setText(conversation.getConversationTitle());
        if (conversation.getMessages() != null && conversation.getMessages().size() > 0) {
            holder.tvContent.setText(conversation.getMessages().get(0).getContent());
        }
        if (conversation.getMessages() != null && conversation.getMessages().size() > 0) {
            PrettyTime prettyTime = new PrettyTime();
            List<BmobIMMessage> messages  = conversation.getMessages();
            Date date = new Date(messages.get(0).getUpdateTime());
            holder.tvTime.setText(prettyTime.format(date));
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClickItemListener(conversation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public SimpleDraweeView ivHeader;
        public TextView tvName;
        public TextView tvContent;
        public TextView tvTime;
        public View root;

        public ViewHolder(View itemView) {
            super(itemView);
            ivHeader = (SimpleDraweeView) itemView.findViewById(R.id.iv_header);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            root = itemView;
        }
    }
}
