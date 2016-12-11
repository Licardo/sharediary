package we.sharediary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import we.sharediary.R;
import we.sharediary.table.WEComment;
import we.sharediary.util.Util;

/**
 * Created by zhanghao on 2016/11/21.
 */

public class WEDetailAdapter extends RecyclerView.Adapter<WEDetailAdapter.CommentViewHolder>{

    private Context mContext;
    private List<WEComment> mComments;
    private PrettyTime mPrettyTime;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public WEDetailAdapter(Context context, List<WEComment> comments) {
        mContext = context;
        mComments = comments;
        mPrettyTime = new PrettyTime(Locale.CHINESE);
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(mContext).inflate(R.layout.adapter_wedetail, null);
        CommentViewHolder holder = new CommentViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        WEComment comment = mComments.get(position);
        holder.tvContent.setText(comment.getContent());
        try {
            String time = comment.getCreatedAt();
            if (Util.isBlank(time)) {
                time = mSimpleDateFormat.format(new Date());
            }
            if (comment.getAuthor() != null) {
                holder.tvAuthor.setText(String.format(mContext.getString(R.string.comment_author_time),
                        comment.getAuthor().getUsername(), mPrettyTime.format(mSimpleDateFormat.parse(time))));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mComments==null?0:mComments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{

        public TextView tvContent;
        public TextView tvAuthor;

        public CommentViewHolder(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvAuthor = (TextView) itemView.findViewById(R.id.tv_author);
        }
    }
}
