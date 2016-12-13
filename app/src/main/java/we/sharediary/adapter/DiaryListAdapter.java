package we.sharediary.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import we.sharediary.R;
import we.sharediary.listener.ClickNameListener;
import we.sharediary.table.WEDiary;
import we.sharediary.ui.WEDetailActivity;
import we.sharediary.ui.WEMainFragment;

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.ViewHolder> {

    private List<WEDiary> mDiaryList;
    private Context mContext;
    private ClickNameListener mListener;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public DiaryListAdapter(List<WEDiary> diaryList, ClickNameListener nameListener) {
        mDiaryList = diaryList;
        mListener = nameListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final WEDiary diary = mDiaryList.get(position);
        String url = "";
        if (diary.getAttachment() != null) {
            url = diary.getAttachment().getUrl();
        }
        holder.ivImg.setImageURI(Uri.parse(url));
        holder.tvContent.setText(diary.getContent());
        String username = "";
        if (diary.getAuthor() != null) {
            username = diary.getAuthor().getUsername();
        }
        holder.tvAuthor.setText(String.format(mContext.getString(R.string.writer_text), username));
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM.d", Locale.ENGLISH);
        try {
            holder.tvDate.setText(dateFormat.format(format.parse(diary.getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (holder.mCardView != null) {
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, WEDetailActivity.class);
                    intent.putExtra(WEMainFragment.DIARY_CONTENT, diary);
                    mContext.startActivity(intent);
                }
            });
        }
        holder.ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClickNameListener(diary.getAuthor());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDiaryList==null?0:mDiaryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView ivImg;
        public TextView tvDate;
        public TextView tvAuthor;
        public TextView tvContent;
        public CardView mCardView;
        public ImageView ivChat;

        public ViewHolder(View view) {
            super(view);
            ivImg = (SimpleDraweeView) view.findViewById(R.id.iv_img);
            tvDate = (TextView) view.findViewById(R.id.tv_date);
            tvAuthor = (TextView) view.findViewById(R.id.tv_author);
            tvContent = (TextView) view.findViewById(R.id.tv_content);
            ivChat = (ImageView) view.findViewById(R.id.iv_chat);
            if (view instanceof CardView) {
                mCardView = (CardView) view;
            }
        }
    }
}
