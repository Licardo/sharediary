package we.sharediary.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.otto.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import we.sharediary.R;
import we.sharediary.base.BaseFragment;
import we.sharediary.base.Constants;
import we.sharediary.result.RefreshEvent;
import we.sharediary.table.WEDiary;
import we.sharediary.table.WEUser;
import we.sharediary.util.BusProviderUtil;
import we.sharediary.util.DialogUtils;

public class WEMainFragment extends BaseFragment {

    private SimpleDraweeView ivImg;
    private TextView tvContent, tvDate, tvWriter, tvTip, tvWeather;
    private CardView mCardView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public static String DIARY_CONTENT = "diary_content";
    private ProgressDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_wemain, null);
        initView(root);
        queryData();
        return root;
    }

    private void initView(View root){
        BusProviderUtil.getBusInstance().register(this);
        ivImg = (SimpleDraweeView) root.findViewById(R.id.iv_img);
        tvContent = (TextView) root.findViewById(R.id.tv_diary_content);
        tvDate = (TextView) root.findViewById(R.id.tv_date);
        tvWriter = (TextView) root.findViewById(R.id.tv_writer);
        tvTip = (TextView) root.findViewById(R.id.tv_tip);
        tvWeather = (TextView) root.findViewById(R.id.tv_weather);
        mCardView = (CardView) root.findViewById(R.id.card_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.srl_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mDialog = DialogUtils.getProgressBar(this.getActivity());
    }

    private void jumpData(final WEDiary diary){
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WEMainFragment.this.getActivity(), WEDetailActivity.class);
                intent.putExtra(DIARY_CONTENT, diary);
                startActivity(intent);
            }
        });
    }

    /**
     * 处理返回结果
     */
    private void handleResult(List<WEDiary> users){
        if (users == null) {
            return;
        }
        if (users.size() >= 1) {
            WEDiary user = users.get(0);
            String imgurl = "";
            if (user.getAttachment() != null) {
                imgurl = user.getAttachment().getUrl();
            }
            ivImg.setImageURI(Uri.parse(imgurl));
            tvContent.setText(user.getContent());
            String username = "";
            if (user.getAuthor() != null) {
                username = user.getAuthor().getUsername();
            }
            tvWriter.setText(String.format(getString(R.string.writer_text), username));
            tvWeather.setText(String.format(getString(R.string.temp_weather), user.getLowtemp(), user.getToptemp(), user.getWeather()));
            tvTip.setText(user.getLovetip());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM.d", Locale.ENGLISH);
            try {
                tvDate.setText(dateFormat.format(format.parse(user.getDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            jumpData(user);
        }
    }

    /**
     * 获取数据
     */
    private void queryData(){
        mDialog.show();
        BmobQuery<WEDiary> query = new BmobQuery<>();
        WEUser user = new WEUser();
        String objectId = (String) readPreferences(Constants.USER_OBJECTID, 0);
        user.setObjectId(objectId);
        query.addWhereEqualTo("author", user);
        query.order("-updatedAt");
        query.setLimit(1);
        query.include("author");
        query.findObjects(new FindListener<WEDiary>() {

            @Override
            public void done(List<WEDiary> list, BmobException e) {
                mDialog.cancel();
                if (list == null) {
                    return;
                }
                if (e == null) {
                    handleResult(list);
                }else {
                    Snackbar.make(tvContent, e.getErrorCode()+"?"+e.getMessage(), Snackbar.LENGTH_LONG).setAction("retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            queryData();
                        }
                    }).show();
                }
            }
        });
    }

    @Subscribe
    public void refreshDialy(RefreshEvent event){
        if (event != null) {
            if (event.getCode() == Constants.REFRESH_CODE) {
                queryData();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProviderUtil.getBusInstance().unregister(this);
    }
}
